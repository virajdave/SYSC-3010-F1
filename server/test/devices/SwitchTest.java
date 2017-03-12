package devices;

import static org.junit.Assert.*;

import org.junit.Test;

public class SwitchTest {

	@Test
	public void testCreate() {
		Device test = Device.createNew(1, 0, null);
		
		assertTrue(test instanceof Switch);
		assertEquals(0, test.getID());
		assertTrue(test.hasID(0));
		assertFalse(test.hasID(50));
	}
}
