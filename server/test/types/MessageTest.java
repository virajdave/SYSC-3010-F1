package types;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import org.junit.Test;

public class MessageTest {

	@Test
	public void testCreate1() {
		InetSocketAddress addr = new InetSocketAddress("localhost", 8080);
		Message m = new Message("Test", addr);
		
		assertEquals(addr, m.getSocketAddress());
		assertEquals(addr.getAddress(), m.getAddress());
		assertEquals(addr.getPort(), m.getPort());
		assertEquals("Test", m.getMessage());
	}

	@Test
	public void testCreate2() {
		InetSocketAddress addr = new InetSocketAddress("10.0.0.12", 25056);
		Message m = new Message("Another message", addr.getAddress(), addr.getPort());
		
		assertEquals(addr, m.getSocketAddress());
		assertEquals(addr.getAddress(), m.getAddress());
		assertEquals(addr.getPort(), m.getPort());
		assertEquals("Another message", m.getMessage());
	}

	@Test
	public void testPacket() throws UnsupportedEncodingException {		
		InetSocketAddress addr = new InetSocketAddress("localhost", 8080);
		byte[] data = "Something GR3AT".getBytes("UTF-8");
		Message m = new Message(new DatagramPacket(data, data.length, addr));
		
		assertEquals(addr, m.getSocketAddress());
		assertEquals(addr.getAddress(), m.getAddress());
		assertEquals(addr.getPort(), m.getPort());
		assertEquals("Something GR3AT", m.getMessage());
	}
	
	@Test
	public void testPacketException() throws UnsupportedEncodingException {
		byte[] data = "Message packet".getBytes("UTF-8");
		try {
			new Message(new DatagramPacket(data, data.length));
			fail("Expected IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("packet requires a valid socket address, port out of range:-1", e.getMessage());
		}
	}

	@Test
	public void testToString1() {
		InetSocketAddress addr = new InetSocketAddress("localhost", 8080);
		Message m = new Message("Test", addr);
		
		assertEquals("localhost/127.0.0.1:8080 Test", m.toString());
	}

	@Test
	public void testToString2() {
		InetSocketAddress addr = new InetSocketAddress("10.0.0.12", 25056);
		Message m = new Message("Another message", addr.getAddress(), addr.getPort());
		
		assertEquals("/10.0.0.12:25056 Another message", m.toString());
	}
}
