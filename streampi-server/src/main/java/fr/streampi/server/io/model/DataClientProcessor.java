package fr.streampi.server.io.model;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class DataClientProcessor implements Runnable {

	protected abstract void onObjectReceived(Object obj);

	private Socket client;
	protected ObjectOutputStream writer;
	private AtomicBoolean isRunning = new AtomicBoolean(false);

	public DataClientProcessor(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		if (client == null) {
			throw new NullPointerException("the client socket is null");
		}
		isRunning.set(true);

		try {
			client.setSoTimeout(3000);
			writer = new ObjectOutputStream(client.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("closing server listener for client : " + client.getInetAddress().getHostName());
			return;
		}
		try (ObjectInputStream reader = new ObjectInputStream(client.getInputStream())) {
			while (isRunning.get() && !client.isClosed()) {
				try {
					Object obj = reader.readObject();
					if (obj != null)
						this.onObjectReceived(obj);
				} catch (SocketException e) {
					e.printStackTrace();
					this.close();
				} catch (EOFException e) {
					this.close();
				} catch (SocketTimeoutException e) {
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (EOFException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void close() throws IOException {
		this.isRunning.set(false);
		this.client.close();
		System.out.println(String.format("client %s successfully disconnected from data socket",
				client.getInetAddress().getHostName()));
	}

	public <T> void sendObject(T obj) throws IOException {
		System.out.println(String.format("sending %s to client %s", obj, client.getInetAddress().getHostName()));
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
