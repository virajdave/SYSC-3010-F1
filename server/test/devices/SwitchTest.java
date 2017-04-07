package devices;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import types.Data;
import types.Web;
import util.DatabaseStub;

public class SwitchTest {
	Device test;
	
	@Before
	public void setup() {
		test = Device.createNew(1, 3, new Web(new DatabaseStub()));
	}

	@Test
	public void testCreate() {
		assertTrue(test instanceof Switch);
		assertEquals(3, test.getID());
		assertTrue(test.hasID(3));
		assertFalse(test.hasID(50));
	}
	
	@Test
	public void testInfo() {
		assertEquals("3/0/0/-1", test.getInfo());
		test.setDead(true);
		assertEquals("3/1/0/-1", test.getInfo());
		test.giveInput(new Data("set", "1"));
		assertEquals("3/1/1/-1", test.getInfo());
	}
	
}
