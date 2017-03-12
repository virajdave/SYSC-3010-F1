package test.devices;

import static org.junit.Assert.*;

import org.junit.Test;
import devices.*;

public class LightsTest {

	@Test
	public void testCreate() {
		Device test = Device.createNew(0, 5, null);
		
		assertTrue(test instanceof Lights);
		assertEquals(5, test.getID());
		assertTrue(test.hasID(5));
		assertFalse(test.hasID(96));
	}

}
