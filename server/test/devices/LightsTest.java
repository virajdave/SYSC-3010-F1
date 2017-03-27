package devices;

import static org.junit.Assert.*;

import org.junit.Test;

import types.Data;

public class LightsTest {

	@Test
	public void testCreate() {
		Device test = Device.createNew(0, 5, null);
		
		assertTrue(test instanceof Lights);
		assertEquals(5, test.getID());
		assertTrue(test.hasID(5));
		assertFalse(test.hasID(96));
	}
	
	@Test
	public void testInfo() {
		Device test = new Lights();
		assertEquals("-1/0/0", test.getInfo());
		

		test = Device.createNew(0, 5, null);
		assertEquals("5/0/0", test.getInfo());
		test.setDead(true);
		assertEquals("5/1/0", test.getInfo());
		test.giveInput(new Data("set", "1"));
		assertEquals("5/1/1", test.getInfo());
	}

}
