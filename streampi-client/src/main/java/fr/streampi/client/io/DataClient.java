package fr.streampi.client.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.streampi.client.io.utils.DataUtils;
import fr.streampi.librairy.model.Layout;
import fr.streampi.librairy.model.ScriptInfo;
import fr.streampi.librairy.utils.ZipUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class DataClient implements Closeable {

	public static final String DEFAULT_ADDRESS = "localhost";
	public static final int DEFAULT_PORT = 9293;
	public static final int DEFAULT_DATA_PORT = 9393;

	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private Thread receiver;

	private ObjectProperty<Layout> layoutProperty = new SimpleObjectProperty<>();

	private Socket socket;
	private Socket dataSocket;

	private ObjectInputStream dataReader;
	private ObjectOutputStream dataWriter;
	private BufferedWriter writer;

	public void connect(InetAddress serverAddress, int port, int dataPort) throws IOException {
		socket = new Socket(serverAddress, port);
		socket.setSoTimeout(1000);

		dataSocket = new Socket(serverAddress, dataPort);
		dataSocket.setSoTimeout(3000);
		writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

		dataWriter = new ObjectOutputStream(dataSocket.getOutputStream());
		start();

	}

	public void start() {
		this.isRunning.set(true);
		receiver = new Thread(() -> {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (IOException e) {
				this.isRunning.set(false);
				e.printStackTrace();
			}
			while (this.isRunning.get() && !this.socket.isClosed() && !this.dataSocket.isClosed()) {
				try {
					String message = reader.readLine();
					if (message != null && !message.equals("")) {
						switch (message) {
						case "LAYOUT":
							try {
								Layout layout = readObject(Layout.class);
								if (layout != null) {
									this.layoutProperty.set(layout);
									System.out.println("layout received");
								}

							} catch (SocketTimeoutException e) {
								System.err.println("no layout received");
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
							break;

						case "ICONS":
							try {
								byte[] b = readObject(byte[].class);
								if (b != null) {
									FileOutputStream writer = new FileOutputStream(DataUtils.zippedIcons);
									writer.write(b);
									writer.close();
									ZipUtils.unzipFile(DataUtils.zippedIcons.toPath(), DataUtils.iconsFolder.toPath());
									System.out.println("stored icons");
								}

							} catch (SocketTimeoutException e) {
								System.err.println("no icons received");
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
							break;
						default:
							System.out.println("message received : " + message);
							break;
						}
					}
				} catch (SocketTimeoutException e) {
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		receiver.start();

	}

	private void sendCommand(String command) throws IOException {
		this.writer.write(command);
		this.writer.write("\r\n");
		this.writer.flush();
	}

	public void sendScriptInfo(ScriptInfo info) throws IOException {
		sendCommand("SCRIPT");
		dataWriter.writeObject(info);
		dataWriter.flush();
	}

	@SuppressWarnings("unchecked")
	private <T> T readObject(Class<T> type) throws ClassNotFoundException, IOException {
		if (dataReader == null)
			dataReader = new ObjectInputStream(dataSocket.getInputStream());
		Object res = dataReader.readObject();
		if (type.isInstance(res))
			return (T) res;

		return null;
	}

	public void close() throws IOException {
		this.sendCommand("EXIT");
		this.isRunning.set(false);
		try {
			receiver.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (dataSocket != null && !dataSocket.isClosed()) {
			if (dataReader != null)
				this.dataReader.close();
			if (dataWriter != null)
				this.dataWriter.close();
			this.dataSocket.close();
		}

		if (socket != null && !socket.isClosed()) {
			if (writer != null)
				this.writer.close();
			this.socket.close();
		}

	}

	public ObjectProperty<Layout> getLayoutProperty() {
		return layoutProperty;
	}

}
