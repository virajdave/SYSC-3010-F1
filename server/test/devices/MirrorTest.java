package devices;

import static org.junit.Assert.*;

import java.io.IOException;

import types.Data;
import org.junit.Test;

public class MirrorTest {

	@Test
	public void testgetColour() {
		Mirror m = new Mirror();
		assertEquals("#2E99A9", m.requestOutput(new Data("colour")).get());
	}
	
	@Test
	public void testsetColourpass() {
		Mirror m = new Mirror();
		String newColour = "#FFFFFF";
		m.giveInput(new Data("colour",newColour));
		assertEquals(newColour, m.requestOutput(new Data("colour")).get());
	}
	
	@Test
	public void testsetColourFail() {
		Mirror m = new Mirror();
		String newColour = "#FFFFFFFFFFFFFFFF";
		m.giveInput(new Data("colour",newColour));
		assertNotEquals(newColour, m.requestOutput(new Data("colour")).get());
	}
	
}
