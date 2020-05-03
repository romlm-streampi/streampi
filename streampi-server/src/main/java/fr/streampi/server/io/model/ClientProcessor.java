package fr.streampi.server.io.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ClientProcessor implements Runnable {

	protected abstract void onMessage(String message);

	protected Socket client;
	private BufferedReader reader;
	protected BufferedWriter writer;
	private AtomicBoolean isRunning = new AtomicBoolean(false);

	public ClientProcessor(Socket client) {
		this.client = client;
	}

	public void run() throws NullPointerException {
		if (client == null) {
			throw new NullPointerException("the client socket is null");
		}
		isRunning.set(true);

		try {
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			client.setSoTimeout(1000);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("closing server listener for client : " + client.getInetAddress().getHostName());
			return;
		}
		while (isRunning.get() && !client.isClosed()) {
			try {
				String message = reader.readLine();
				if (message != null)
					this.onMessage(message);
			} catch (SocketTimeoutException e) {
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			if (this.reader != null)
				reader.close();
			if (this.writer != null)
				writer.close();
			this.client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(String.format("client %s successfully disconnected", client.getInetAddress().getHostName()));

	}

	public void close() throws IOException {
		this.isRunning.set(false);
	}

	public void sendMessage(String message) throws IOException {
		if (this.writer == null)
			this.writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		this.writer.write(message);
		this.writer.newLine();
		this.writer.flush();
	}

	public Socket getClient() {
		return client;
	}

}
