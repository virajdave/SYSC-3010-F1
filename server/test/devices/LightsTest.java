package devices;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import types.Data;
import types.Web;
import util.DatabaseStub;

public class LightsTest {
	Device test;
	
	@Before
	public void setup() {
		test = Device.createNew(0, 5, new Web(new DatabaseStub()));
	}

	@Test
	public void testCreate() {
		assertTrue(test instanceof Lights);
		assertEquals(5, test.getID());
		assertTrue(test.hasID(5));
		assertFalse(test.hasID(96));
	}
	
	@Test
	public void testInfo() {
		assertEquals("5/0/0", test.getInfo());
		test.setDead(true);
		assertEquals("5/1/0", test.getInfo());
		test.giveInput(new Data("set", "1"));
		assertEquals("5/1/1", test.getInfo());
	}

}
