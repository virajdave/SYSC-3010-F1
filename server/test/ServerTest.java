import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

import org.junit.Before;
import org.junit.Test;

import types.Message;

public class ServerTest {
	Server server;
	DatagramSocket socket;
	InetSocketAddress serverAddr, socketAddr;
	
	@Before
	public void setup() throws SocketException, InterruptedException {		
		server = new Server(null);
		server.start();
		do {
			Thread.sleep(1);
		} while(server.getPort() == null);
		serverAddr = new InetSocketAddress("127.0.0.1", server.getPort());
		
		socket = new DatagramSocket();
		socketAddr = new InetSocketAddress("127.0.0.1", socket.getLocalPort());
	}

	@Test
	public void testStartStop() {
		assertTrue(server.isAlive());
		server.interrupt();
		assertFalse(server.isAlive());
	}

	@Test
	public void testRecv0() throws InterruptedException {
        // Give the server a chance to run.
		Thread.sleep(1);
		
        Message m = server.recvMessage();
        assertNull(m);
	}

	@Test
	public void testRecv1() throws IOException, InterruptedException {
        byte[] sendData = "Testing message".getBytes("UTF-8");
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr);
        socket.send(sendPacket);

        // Give the server a chance to run.
		Thread.sleep(1);
		
        Message m = server.recvMessage();
        assertEquals("Testing message", m.getMessage());
        assertEquals(socketAddr, m.getSocketAddress());
	}

	@Test
	public void testRecv2() throws IOException, InterruptedException {
		for (int i = 0; i < 10; i++) {
	        byte[] sendData = ("Test" + i).getBytes("UTF-8");
	        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr);
	        socket.send(sendPacket);
		}

        // Give the server a chance to run.
		Thread.sleep(1);

		for (int i = 0; i < 10; i++) {
	        Message m = server.recvMessage();
	        assertEquals("Test" + i, m.getMessage());
	        assertEquals(socketAddr, m.getSocketAddress());
		}
	}

}
