package devices;

import static org.junit.Assert.*;

import org.junit.Test;

import types.Data;

public class SwitchTest {

	@Test
	public void testCreate() {
		Device test = Device.createNew(1, 0, null);
		
		assertTrue(test instanceof Switch);
		assertEquals(0, test.getID());
		assertTrue(test.hasID(0));
		assertFalse(test.hasID(50));
	}
	
	@Test
	public void testInfo() {
		Device test = new Switch();
		assertEquals("-1/0/0/-1", test.getInfo());
		
		test = Device.createNew(1, 5, null);
		assertEquals("5/0/0/-1", test.getInfo());
		test.setDead(true);
		assertEquals("5/1/0/-1", test.getInfo());
		test.giveInput(new Data("set", "1"));
		assertEquals("5/1/1/-1", test.getInfo());
	}
	
}
