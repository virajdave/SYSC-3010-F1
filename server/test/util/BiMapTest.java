package util;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

public class BiMapTest {
	BiMap<String, Integer> map;
	
	@Before
	public void setup() {
		map = new BiMap<>();
		map.put("Bill", 2);
		map.put("Bobby", 55);
		map.put("Ben", 420);
	}

	@Test
	public void testCreate() {
		assertEquals(new Integer(2), map.get("Bill"));
		assertEquals(new Integer(55), map.get("Bobby"));
		assertEquals(new Integer(420), map.get("Ben"));
	}

	@Test
	public void testCreateFromMap() {
		HashMap<String, Integer> data = new HashMap<>();
		data.put("Bill", 2);
		data.put("Bobby", 55);
		data.put("Ben", 420);
		
		map = new BiMap<>(data);
		assertEquals(new Integer(2), map.get("Bill"));
		assertEquals(new Integer(55), map.get("Bobby"));
		assertEquals(new Integer(420), map.get("Ben"));
	}

	@Test
	public void testClearEmpty() {
		assertFalse(map.isEmpty());
		assertEquals(3, map.size());
		map.clear();
		assertTrue(map.isEmpty());
		assertEquals(0, map.size());
		map.put("Jimmy", 10);
		assertFalse(map.isEmpty());
		assertEquals(1, map.size());
	}

	@Test
	public void testInverse() {
		assertEquals("Bill", map.inverse().get(2));
		assertEquals("Bobby", map.inverse().get(55));
		assertEquals("Ben", map.inverse().get(420));
	}

	@Test
	public void testPut() {
		assertEquals(new Integer(2), map.get("Bill"));
		map.put("Willy", 420);
		map.put("Jimmy", 2);
		assertNull(map.get("Bill"));
		assertNull(map.get("Ben"));
		assertEquals(new Integer(2), map.get("Jimmy"));
		assertEquals(new Integer(55), map.get("Bobby"));
		assertEquals(new Integer(420), map.get("Willy"));
		
		map.inverse().put(55, "Connor");
		map.inverse().put(80, "Willy");
		assertNull(map.inverse().get(420));
		assertEquals(new Integer(2), map.get("Jimmy"));
		assertEquals(new Integer(55), map.get("Bobby"));
		assertEquals(new Integer(80), map.get("Willy"));
	}

	@Test
	public void testContains() {
		assertTrue(map.containsKey("Bill"));
		assertTrue(map.containsValue(2));
		assertFalse(map.containsKey("Jimmy"));
		assertFalse(map.containsValue(25));

		assertTrue(map.inverse().containsKey(2));
		assertTrue(map.inverse().containsValue("Bill"));
		assertFalse(map.inverse().containsKey(25));
		assertFalse(map.inverse().containsValue("Jimmy"));
	}

	@Test
	public void testRemove() {
		assertEquals(3, map.size());
		assertEquals(new Integer(2), map.remove("Bill"));
		assertNull(map.get("Bill"));
		assertNull(map.inverse().get(2));
		assertEquals(2, map.size());
		
		assertEquals("Ben", map.inverse().remove(420));
		assertNull(map.get("Ben"));
		assertNull(map.inverse().get(420));
		assertEquals(1, map.size());
	}

}
