package util;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

public class BiMapTest {

	@Test
	public void test() {
		BiMap<String, Integer> map;
		
		map = new BiMap<>();
		map.put("Bill", 2);
		map.put("Bobby", 55);
		map.put("Ben", 420);
		assertEquals(new Integer(2), map.get("Bill"));
		assertEquals(new Integer(55), map.get("Bobby"));
		assertEquals(new Integer(420), map.get("Ben"));
		
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
	public void testInverse() {
		BiMap<String, Integer> map;
		
		map = new BiMap<>();
		map.put("Bill", 2);
		map.put("Bobby", 55);
		map.put("Ben", 420);
		assertEquals("Bill", map.inverse().get(2));
		assertEquals("Bobby", map.inverse().get(55));
		assertEquals("Ben", map.inverse().get(420));
	}

}
