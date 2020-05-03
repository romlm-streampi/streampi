package fr.streampi.client.io;

import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.streampi.librairy.model.Layout;

class DataClientTest {

	private DataClient client = new DataClient();
	private ServerSocket server;
	private ServerSocket dataServer;
	private Socket socket;
	private Socket dataSocket;

	@BeforeEach
	void setUp() throws Exception {
		InetAddress addr = InetAddress.getLoopbackAddress();
		int port = 9293;
		int dataPort = 9393;
		server = new ServerSocket(port, 1, addr);
		dataServer = new ServerSocket(dataPort, 1, addr);

		client.connect(addr, port, dataPort);
		socket = server.accept();
		dataSocket = dataServer.accept();

		System.out.println("accepted");
	}

	@AfterEach
	void tearDown() throws Exception {

		client.close();
		socket.close();
		dataSocket.close();
		server.close();
		dataServer.close();
	}

	@Test
	void testConnect() throws Exception {
		socket.getOutputStream().write("LAYOUT\r\n".getBytes());
		socket.getOutputStream().flush();

		ObjectOutputStream writer = new ObjectOutputStream(dataSocket.getOutputStream());
		Layout test = new Layout();
		writer.writeObject(test);
		writer.flush();
	}

}
