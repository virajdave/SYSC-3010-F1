package types;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import util.Database;

public class DatabaseTest {
	private static Database db;
	
	@BeforeClass
	public static void setItUp() {
		db = new Database("test");
	}

	@Before
	public void setUp() {
		assertTrue(db.create());
	}

	@Test
	public void testCreateExists1() {
		assertTrue(db.exists());
	}

	@Test
	public void testCreateExists2() {
		// Delete the db file, then check that the db doesn't exist on start up.
		db.close();
		try {
			Files.delete(new File("test.db").toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		db = new Database("test");
		
		assertFalse(db.exists());
	}

	@Test
	public void testGetName() {
		assertEquals("test", db.getName());
	}

	@Test
	public void testAddDevice1() {
		assertTrue(db.addDevice(2, 5, "localhost:3010"));
		assertTrue(db.addDevice(6, 0, "127.0.0.1:5050"));
		
		HashMap<Integer, Entry<Integer, String>> devices = db.getDevices();
		int count = 2;
		assertEquals(count, devices.size());
		
		for (Entry<Integer, Entry<Integer, String>> device : devices.entrySet()) {
			if ((device.getKey() == 2 && device.getValue().getKey() == 5 && device.getValue().getValue().equals("localhost:3010"))
			 || (device.getKey() == 6 && device.getValue().getKey() == 0 && device.getValue().getValue().equals("127.0.0.1:5050"))) {
				count--;
			}
		}
		assertEquals(0, count);
	}

	@Test
	public void testAddDevice2() {
		assertTrue(db.addDevice(2, 5, "localhost:3010"));
		assertTrue(db.addDevice(2, 0, "127.0.0.1:5050"));
		
		HashMap<Integer, Entry<Integer, String>> devices = db.getDevices();
		int count = 1;
		assertEquals(count, devices.size());
		
		assertEquals(new Integer(0), devices.get(2).getKey());
		assertEquals("127.0.0.1:5050", devices.get(2).getValue());
	}

	@Test
	public void testRemoveDevice() {
		assertTrue(db.addDevice(2, 5, "localhost:3010"));
		assertTrue(db.addDevice(6, 0, "127.0.0.1:5050"));
		assertTrue(db.removeDevice(2));
		assertTrue(db.removeDevice(3));
		
		HashMap<Integer, Entry<Integer, String>> devices = db.getDevices();
		int count = 1;
		assertEquals(count, devices.size());
		
		assertEquals(new Integer(0), devices.get(6).getKey());
		assertEquals("127.0.0.1:5050", devices.get(6).getValue());
	}

	@Test
	public void testAddProp1() {
		assertTrue(db.addProp(1, "set", "true"));
		assertTrue(db.addProp(3, "set", "false"));
		assertTrue(db.addProp(1, "another", "nope"));
		
		HashMap<Integer, HashMap<String, String>> devices = db.getProp();
		int count = 3;
		assertEquals(2, devices.size());
		
		for (Entry<Integer, HashMap<String, String>> device : devices.entrySet()) {
			// Check the size of the properties of each device is correct.
			if (device.getKey() == 1) {
				assertEquals(2, device.getValue().size());
			} else if (device.getKey() == 3) {
				assertEquals(1, device.getValue().size());
			}
			
			// Loop through each property decrementing if everything matches.
			for (Entry<String, String> property : device.getValue().entrySet()) {
				if ((device.getKey() == 1 && property.getKey().equals("set") && property.getValue().equals("true"))
				 || (device.getKey() == 3 && property.getKey().equals("set") && property.getValue().equals("false"))
				 || (device.getKey() == 1 && property.getKey().equals("another") && property.getValue().equals("nope"))) {
					count--;
				}
			}
		}
		assertEquals(0, count);
	}
}
