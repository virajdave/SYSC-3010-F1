package util;

import static org.junit.Assert.*;

import org.junit.Test;

import util.Parse;

public class ParseTest {

	@Test
	public void testToBool() {
		assertTrue(Parse.toBool("1"));
		assertFalse(Parse.toBool("0"));
	}

	@Test
	public void testToBoolException() {
		try {
			Parse.toBool("a"); fail("Expected IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("'a' is not '1' or '0' and cannot be parsed to boolean.", e.getMessage());
		}
		try {
			Parse.toBool("true"); fail("Expected IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("'true' is not '1' or '0' and cannot be parsed to boolean.", e.getMessage());
		}
		try {
			Parse.toBool("false"); fail("Expected IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("'false' is not '1' or '0' and cannot be parsed to boolean.", e.getMessage());
		}
	}

	@Test
	public void testToInt() {
		assertEquals(1, Parse.toInt("1"));
		assertEquals(0, Parse.toInt("0"));
		assertEquals(96, Parse.toInt("96"));
		assertEquals(25905, Parse.toInt("25905"));
		assertEquals(-54, Parse.toInt("-54"));
	}

	@Test
	public void testToIntException() {
		try {
			Parse.toInt("a"); fail("Expected NumberFormatException to be thrown");
		} catch (NumberFormatException e) {
			assertEquals("For input string: \"a\"", e.getMessage());
		}
		try {
			Parse.toInt("true"); fail("Expected NumberFormatException to be thrown");
		} catch (NumberFormatException e) {
			assertEquals("For input string: \"true\"", e.getMessage());
		}
		try {
			Parse.toInt("false"); fail("Expected NumberFormatException to be thrown");
		} catch (NumberFormatException e) {
			assertEquals("For input string: \"false\"", e.getMessage());
		}
	}

	@Test
	public void testToStringBool() {
		assertEquals("1", Parse.toString(true));
		assertEquals("0", Parse.toString(false));
	}

	@Test
	public void testToStringInt() {
		assertEquals("1", Parse.toString(1));
		assertEquals("0", Parse.toString(0));
		assertEquals("96", Parse.toString(96));
		assertEquals("25905", Parse.toString(25905));
		assertEquals("-54", Parse.toString(-54));
	}

	@Test
	public void testToStringChar() {
		assertEquals("1", Parse.toString('1'));
		assertEquals("c", Parse.toString('c'));
		assertEquals("\\", Parse.toString('\\'));
		assertEquals("-", Parse.toString('-'));
	}

	@Test
	public void testToStringArray() {
		assertEquals("101", Parse.toString("", true, false, true));
		assertEquals("-95054", Parse.toString("", -95, 0, 54));
		assertEquals("test", Parse.toString(":aa", "test"));
		assertEquals("1/0", Parse.toString("/", true, false));
		assertEquals("-88+1+no", Parse.toString("+", -88, true, "no"));
		assertEquals("Bbreak0breakthis", Parse.toString("break", 'B', '0', "this"));
	}

	@Test
	public void testToStringArrayException() {
		try {
			Parse.toString("", new Parse()); fail("Expected IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("Cannot parse index 0 of type 'util.Parse'", e.getMessage());
		}
		try {
			Parse.toString("", true, "test", new Parse()); fail("Expected IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("Cannot parse index 2 of type 'util.Parse'", e.getMessage());
		}
		try {
			Parse.toString("", 420, new Object(), 58, new Parse()); fail("Expected IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("Cannot parse index 1 of type 'java.lang.Object'", e.getMessage());
		}
	}

}
