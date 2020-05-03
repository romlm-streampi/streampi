package fr.streampi.client.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import fr.streampi.librairy.model.Layout;
import fr.streampi.librairy.model.ScriptInfo;
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
		// cannot initialize dataReader : blocking
		// TODO : fix the bug
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
						System.out.println("message is : " + message);
						switch (message) {
						case "LAYOUT":
							try {
								Layout layout = readObject(Layout.class);
								System.out.println(layout);
								if (layout != null)
									this.layoutProperty.set(layout);

							} catch (SocketTimeoutException e) {
								System.err.println("no layout received");
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
							break;

						case "ICONS":
							// TODO save icons to the right file
							System.out.println("should save icons");
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
