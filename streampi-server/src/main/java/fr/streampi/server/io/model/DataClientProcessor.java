package fr.streampi.server.io.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class DataClientProcessor {

	private Socket client;
	private ObjectOutputStream writer;
	private ObjectInputStream reader;

	public DataClientProcessor(Socket client) {
		this.client = client;
	}

	public void close() throws IOException {
		if (this.writer != null)
			this.writer.close();
		if (this.reader != null)
			this.reader.close();
		this.client.close();
		System.out.println(String.format("client %s successfully disconnected from data socket",
				client.getInetAddress().getHostName()));
	}

	@SuppressWarnings("unchecked")
	public <T> T readObject(Class<T> type) throws IOException, ClassNotFoundException {
		if (this.reader == null)
			this.reader = new ObjectInputStream(client.getInputStream());
		Object result = reader.readObject();
		if (type.isInstance(result))
			return (T) result;
		return null;
	}

	public <T> void sendObject(T obj) throws IOException {
		System.out.println(String.format("sending %s to client %s", obj.getClass().getName(), client.getInetAddress().getHostName()));
		if (this.writer == null)
			this.writer = new ObjectOutputStream(client.getOutputStream());
		this.writer.writeObject(obj);
		this.writer.flush();
		System.out.println(
				String.format("successfully sended object to client %s", client.getInetAddress().getHostName()));
	}

	public Socket getClient() {
		return client;
	}

}
