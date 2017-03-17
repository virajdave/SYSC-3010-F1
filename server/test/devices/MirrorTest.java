package devices;

import static org.junit.Assert.*;

import org.junit.Test;

public class MirrorTest {

	@Test
	public void testgetColour() {
		Mirror m = new Mirror();
		assertEquals("#2E99A9", m.getColour());
	}
	
	@Test
	public void testsetColourpass() {
		Mirror m = new Mirror();
		String newColour = "#FFFFFF";
		m.setColour(newColour);
		assertEquals(newColour, m.getColour());
	}
	
	@Test
	public void testsetColourFail() {
		Mirror m = new Mirror();
		String newColour = "#FFFFFFFFFFFFFFFF";
		m.setColour(newColour);
		assertNotEquals(newColour, m.getColour());
	}

	
}
