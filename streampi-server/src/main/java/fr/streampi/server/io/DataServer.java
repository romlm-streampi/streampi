package fr.streampi.server.io;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import fr.streampi.librairy.model.Layout;
import fr.streampi.librairy.model.ScriptInfo;
import fr.streampi.librairy.utils.ZipUtils;
import fr.streampi.server.io.model.Client;
import fr.streampi.server.io.model.ClientProcessor;
import fr.streampi.server.io.model.DataClientProcessor;
import fr.streampi.server.io.utils.DataUtils;

public class DataServer implements Closeable {

	public static final String DEFAULT_ADDRESS = "localhost";
	public static final int DEFAULT_PORT = 9293;
	public static final int DEFAULT_DATA_PORT = 9393;

	private static final int DEFAULT_BACKLOG = 10;

	private Layout layout;

	private List<Client> clients = Collections.synchronizedList(new ArrayList<>());
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private Thread receiver;
	private Consumer<ScriptInfo> onScriptReceived;

	private ServerSocket server;
	private ServerSocket dataSocket;

	public void bind(InetAddress address, int port, int dataPort) throws IOException {
		this.server = new ServerSocket(port, DEFAULT_BACKLOG, address);
		System.out.println(
				String.format("server successfully bound to address %s at port %d", address.getHostName(), port));
		server.setSoTimeout(1000);
		this.dataSocket = new ServerSocket(dataPort, DEFAULT_BACKLOG, address);
		this.dataSocket.setSoTimeout(3000);
		System.out.println(String.format("data socket successfully bound to address %s at port %d",
				address.getHostName(), dataPort));
		start();
	}

	private void start() {
		this.isRunning.set(true);

		Runnable dataRunnable = () -> {
			while (!server.isClosed() && isRunning.get()) {
				try {
					Socket dataClient = dataSocket.accept();
					System.out.println(String.format("client %s successfully connected to data socket",
							dataClient.getInetAddress().getHostName()));

					DataClientProcessor processor = new DataClientProcessor(dataClient);

					synchronized (clients) {
						boolean clientExists = false;
						for (Client client : this.clients) {
							if (client.getAddress().equals(dataClient.getInetAddress())) {
								client.setDataProcessor(processor);
								clientExists = true;
								if (client.getProcessor() != null) {
									if (client.getProcessor().getClient().isClosed()) {
										client.setProcessor(null);
									} else {
										this.sendLayout(client);
										this.sendIcons(client);
									}
									break;
								}
							}
						}
						if (!clientExists) {
							Client client = new Client(dataClient.getInetAddress(), null, processor);
							this.clients.add(client);
						}
					}
				} catch (SocketTimeoutException e) {
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};

		Thread dataThread = new Thread(dataRunnable);
		dataThread.start();

		Runnable textStreamRunnable = () -> {
			System.out.println("server started listening for connections");
			while (!server.isClosed() && isRunning.get()) {
				try {
					Socket clientSocket = server.accept();
					System.out.println(String.format("client %s successfully connected",
							clientSocket.getInetAddress().getHostName()));

					ClientProcessor processor = new ClientProcessor(clientSocket) {

						@Override
						public void onMessage(String message) {
							switch (message) {
							case "SCRIPT":
								Optional<Client> optClient = clients.stream()
										.filter(c -> c.getAddress().equals(clientSocket.getInetAddress())).findFirst();
								if (optClient.isPresent()) {
									try {
										ScriptInfo info = optClient.get().getDataProcessor()
												.readObject(ScriptInfo.class);
										if (info != null && onScriptReceived != null)
											onScriptReceived.accept(info);
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								break;
								
								default:
									System.out.println(String.format("received %s from client %s", message, clientSocket.getInetAddress().getHostName()));
									break;
							}
						}

					};

					synchronized (clients) {
						boolean clientExists = false;
						for (Client client : this.clients) {
							if (client.getAddress().equals(clientSocket.getInetAddress())) {
								client.setProcessor(processor);
								if (client.getDataProcessor() != null) {
									if (client.getDataProcessor().getClient().isClosed()) {
										client.setProcessor(null);
									} else {
										this.sendLayout(client);
										this.sendIcons(client);
									}
									break;
								}
							}
						}
						if (!clientExists) {
							Client client = new Client(clientSocket.getInetAddress(), processor, null);
							this.clients.add(client);
						}
					}

					Thread clientThread = new Thread(processor);
					clientThread.start();
				} catch (SocketTimeoutException e) {
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				dataThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		};
		receiver = new Thread(textStreamRunnable);
		receiver.start();
	}

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	protected void sendLayout(Client client) throws IOException {
		if (isClientReady(client)) {
			client.getProcessor().sendMessage("LAYOUT");
			System.out.println(String.format("sent layout header to client %s", client.getAddress().getHostName()));
			client.getDataProcessor().sendObject(layout);
			System.out.println(String.format("sent layout to client %s", client.getAddress().getHostName()));
		} else {
			System.out.println(String.format("client %s is not connected to both sockets", client.getAddress()));
		}

	}

	protected void sendIcons(Client client) throws IOException {
		if (isClientReady(client)) {
			ZipUtils.zipFolder(DataUtils.iconsFolder.toPath(), DataUtils.iconsZipFile.toPath());
			FileInputStream reader = new FileInputStream(DataUtils.iconsZipFile);
			byte[] b = reader.readAllBytes();
			reader.close();
			client.getProcessor().sendMessage("ICONS");
			System.out.println("sent icons header to " + client.getAddress());
			client.getDataProcessor().<byte[]>sendObject(b);
			System.out.println("sent icons zip file to client " + client.getAddress());
		} else {
			System.out.println(String.format("client %s is not connected to both sockets", client.getAddress()));
		}
	}

	public void sendIcons() throws IOException {
		synchronized (clients) {
			for (Client client : clients)
				sendIcons(client);
		}
	}

	public void updateLayout() throws IOException {
		synchronized (clients) {
			for (Client client : this.clients) {
				sendLayout(client);
			}
		}

	}

	private boolean isClientReady(Client client) {
		try {
			if (client.getDataProcessor() == null || client.getDataProcessor().getClient().isClosed())
				return false;
			if (client.getProcessor() == null || client.getProcessor().getClient().isClosed())
				return false;
		} catch (NullPointerException e) {
			return false;
		}
		return true;
	}

	public Consumer<ScriptInfo> getOnScriptReceived() {
		return onScriptReceived;
	}

	public void setOnScriptReceived(Consumer<ScriptInfo> onScriptReceived) {
		this.onScriptReceived = onScriptReceived;
	}

	public void close() throws IOException {
		this.isRunning.set(false);
		try {
			this.receiver.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (Client client : this.clients) {
			if (client.getDataProcessor() != null)
				client.getDataProcessor().close();
			if (client.getProcessor() != null)
				client.getProcessor().close();
		}
		server.close();
		System.out.println("server successfully closed");
	}

}
