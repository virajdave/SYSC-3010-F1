package types;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import org.junit.Test;

public class MessageTest {

	@Test
	public void test() {
		InetSocketAddress addr;
		Message m;
		
		addr = new InetSocketAddress("localhost", 8080);
		m = new Message("Test", addr);
		assertEquals(addr, m.getSocketAddress());
		assertEquals(addr.getAddress(), m.getAddress());
		assertEquals(addr.getPort(), m.getPort());
		assertEquals("Test", m.getMessage());

		addr = new InetSocketAddress("10.0.0.12", 25056);
		m = new Message("Another message", addr.getAddress(), addr.getPort());
		assertEquals(addr, m.getSocketAddress());
		assertEquals(addr.getAddress(), m.getAddress());
		assertEquals(addr.getPort(), m.getPort());
		assertEquals("Another message", m.getMessage());
	}

	@Test
	public void testPacket() throws UnsupportedEncodingException {
		InetSocketAddress addr;
		Message m;
		byte[] data;
		
		addr = new InetSocketAddress("localhost", 8080);
		data = "Something GR3AT".getBytes("UTF-8");
		m = new Message(new DatagramPacket(data, data.length, addr));
		assertEquals(addr, m.getSocketAddress());
		assertEquals(addr.getAddress(), m.getAddress());
		assertEquals(addr.getPort(), m.getPort());
		assertEquals("Something GR3AT", m.getMessage());

		try {
			data = "Message packet".getBytes("UTF-8");
			m = new Message(new DatagramPacket(data, data.length));
			assertEquals(addr, m.getSocketAddress());
			assertEquals(addr.getAddress(), m.getAddress());
			assertEquals(addr.getPort(), m.getPort());
			assertEquals("Something GR3AT", m.getMessage());
			fail("Expected IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("packet requires a valid socket address, port out of range:-1", e.getMessage());
		}
	}

	@Test
	public void testToString() {
		InetSocketAddress addr;
		Message m;
		
		addr = new InetSocketAddress("localhost", 8080);
		m = new Message("Test", addr);
		assertEquals("localhost/127.0.0.1:8080 Test", m.toString());

		addr = new InetSocketAddress("10.0.0.12", 25056);
		m = new Message("Another message", addr.getAddress(), addr.getPort());
		assertEquals("/10.0.0.12:25056 Another message", m.toString());
	}
}
