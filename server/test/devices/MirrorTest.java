package devices;

import static org.junit.Assert.*;

import java.io.IOException;

import types.Data;
import types.Web;
import util.DatabaseStub;

import org.junit.Before;
import org.junit.Test;

public class MirrorTest {
	Device test;
	
	@Before
	public void setup() {
		test = Device.createNew(2, 7, new Web(new DatabaseStub()));
	}

	@Test
	public void testgetColour() {
		assertEquals("#2E99A9", test.requestOutput(new Data("colour")).get());
	}
	
	@Test
	public void testsetColourpass() {
		String newColour = "#FFFFFF";
		test.giveInput(new Data("colour",newColour));
		assertEquals(newColour, test.requestOutput(new Data("colour")).get());
	}
	
	@Test
	public void testsetColourFail() {
		String newColour = "#FFFFFFFFFFFFFFFF";
		test.giveInput(new Data("colour",newColour));
		assertNotEquals(newColour, test.requestOutput(new Data("colour")).get());
	}
	
}
