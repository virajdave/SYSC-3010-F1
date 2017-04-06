package main;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;
import java.util.Base64;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import types.Message;
import util.DatabaseStub;
import util.Log;

public class ManagerTest {
	private static final int LENGTH_SCALE = 50;
	private static final int ITERATION_SCALE = 5000;
	private static final InetSocketAddress ADDR1 = new InetSocketAddress("localhost", 99);

	private Manager m;

	@Before
	public void setup() {
		Log.onlyError(true);
		Server s = new Server();
		m = new Manager(s, new DatabaseStub());
		s.start();
		
		// Let server start up.
		int timeout = 10; 
		while (!s.isAlive() && timeout-- != 0) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}

	@Test
	public void sendGarbage() {
		String message = "";
		byte[] b;
		
		try {
			for (int x = 0; x < LENGTH_SCALE; x++) {
				b = new byte[x];
						
				for (int y = 0; y < ITERATION_SCALE; y++) {
					new Random().nextBytes(b);
					message = new String(b, 0, b.length);
					m.gotMessage(new Message(message, ADDR1));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed with message: '" + message + "'", e);
		}
	}

	@Test
	public void sendEncodedGarbage() {
		String message = "";
		byte[] b;
		
		try {
			for (int x = 0; x < LENGTH_SCALE; x++) {
				b = new byte[x];
						
				for (int y = 0; y < ITERATION_SCALE; y++) {
					new Random().nextBytes(b);
					message = Base64.getEncoder().encodeToString(b);
					m.gotMessage(new Message(message, ADDR1));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed with message: '" + message + "'", e);
		}
	}

}
