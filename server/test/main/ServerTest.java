package main;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import main.Server;
import types.Message;

public class ServerTest {
	private final static int ITERATIONS = 100;
	private final static int TIMEOUT = 500;
	
	private Server server;
	private InetSocketAddress serverAddr;
	private static DatagramSocket socket;
	private static InetSocketAddress socketAddr;
	
	@BeforeClass
	public static void setupSocket() throws SocketException {
		socket = new DatagramSocket();
		socket.setSoTimeout(1000);
		socketAddr = new InetSocketAddress("127.0.0.1", socket.getLocalPort());
	}
	
	@Before
	public void setupServer() throws SocketException, InterruptedException {
		server = new Server(null);
		server.start();
		do {
			Thread.sleep(1);
		} while(server.getPort() == null);
		serverAddr = new InetSocketAddress("127.0.0.1", server.getPort());
	}
	
	@AfterClass
	public static void teardownSocket() {
		socket.close();
	}
	
	@After
	public void teardownServer() throws InterruptedException {
		server.interrupt();
		server.join(5000);
	}

	@Test
	public void testRecvMessageNull() throws InterruptedException {
		// Give the server a chance to run.
		Thread.sleep(10);
		
		Message m = server.recvMessage();
		assertNull(m);
	}

	@Test
	public void testRecvMessage() throws IOException, InterruptedException {
		byte[] sendData = "Testing message".getBytes("UTF-8");
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr);
		socket.send(sendPacket);

		Message m = busyWaitMessage();
		assertEquals("Testing message", m.getMessage());
		assertEquals(socketAddr, m.getSocketAddress());
	}

	@Test
	public void testRecvMessageStress() throws IOException, InterruptedException {
		for (int i = 0; i < ITERATIONS; i++) {
			byte[] sendData = ("Test" + i).getBytes("UTF-8");
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr);
			socket.send(sendPacket);
		}
		
		for (int i = 0; i < ITERATIONS; i++) {
			Message m = busyWaitMessage();
			assertNotNull(m);
			assertEquals("Test" + i, m.getMessage());
			assertEquals(socketAddr, m.getSocketAddress());
		}
	}

	@Test
	public void testRecvMessageOverflow() throws IOException, InterruptedException {
		byte[] sendData = new byte[1600];
		new Random().nextBytes(sendData);
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr);
		socket.send(sendPacket);
		
		Message m = busyWaitMessage();
		assertNotNull(m);
		assertEquals(new String(sendData, 0, 1500), m.getMessage());
		assertEquals(socketAddr, m.getSocketAddress());
	}

	@Test
	public void testRecvWait() {
		int wait = 200;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(wait * 4);
					if (!Thread.interrupted()) {
						fail("test timeout -> recvWait took too long to complete, may have missed a message.");
					}
				} catch (InterruptedException e) {
					if (!e.getMessage().toLowerCase().equals("sleep interrupted")) {
						e.printStackTrace();
					}
				}
			}
		});
		t.start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(wait);
					byte[] sendData = ("Testing message").getBytes("UTF-8");
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr);
					socket.send(sendPacket);
				} catch (IOException | InterruptedException e) {
					if (e.getMessage().toLowerCase().equals("socket is closed")) {
						System.err.println("Stopped sending messages since socket closed, most likely due to a failed test.");
					} else {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		long startTime = System.currentTimeMillis();
		Message m = server.recvWait(TIMEOUT);
		long endTime   = System.currentTimeMillis();
		assertNotNull(m);
		assertEquals("Testing message", m.getMessage());
		assertEquals(socketAddr, m.getSocketAddress());
		assertTrue(wait - endTime + startTime < 20);
		t.interrupt();
	}

	@Test
	public void testRecvWaitStress() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(ITERATIONS * 4);
					if (!Thread.interrupted()) {
						fail("test timeout -> recvWait took too long to complete, may have missed a message.");
					}
				} catch (InterruptedException e) {
					if (!e.getMessage().toLowerCase().equals("sleep interrupted")) {
						e.printStackTrace();
					}
				}
			}
		});
		t.start();
		
		new Thread(new Runnable() {
			@Override
            public void run() {
            	try {
    	        	for (int i = 0; i < ITERATIONS; i++) {
	        			byte[] sendData = ("Test" + i).getBytes("UTF-8");
	        			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr);
	        			socket.send(sendPacket);
	        		}
        			Thread.sleep(1);
				} catch (IOException | InterruptedException e) {
					if (e.getMessage().toLowerCase().equals("socket is closed")) {
						System.err.println("Stopped sending messages since socket closed, most likely due to a failed test.");
					} else {
						e.printStackTrace();
					}
				}
            }
        }).start();

		for (int x = 0; x < ITERATIONS; x++) {
			Message m = server.recvWait(TIMEOUT);
			assertNotNull(m);
			assertEquals("Test" + x, m.getMessage());
			assertEquals(socketAddr, m.getSocketAddress());
		}
		t.interrupt();
	}

	@Test
	public void testSendMessage() throws IOException {
		server.sendMessage(new Message("Server sent message", socketAddr));
		
		byte[] data = new byte[1500];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		socket.receive(packet);
		
		assertEquals("Server sent message", new String(packet.getData(), 0, packet.getLength()));
		assertEquals(serverAddr, packet.getSocketAddress());
	}

	@Test
	public void testSendMessageNull() {
		try {
			server.sendMessage(null);
			fail("Expected NullPointerException to be thrown");
		} catch (NullPointerException e) {}
	}
	
	private Message busyWaitMessage() {
		int i = 5;
		Message m;
		while ((m = server.recvMessage()) == null && i > 0) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		return m;
	}

}
