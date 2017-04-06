package types;

import static org.junit.Assert.*;

import java.net.InetSocketAddress;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import devices.Device;
import devices.Lights;
import devices.Switch;
import util.DatabaseStub;

public class WebTest {
	private static int index;
	private static InetSocketAddress[] addr;
	private Device[] d;
	private Web web;
	
	@BeforeClass
	public static void setupAddrs() {
		addr = new InetSocketAddress[]{
			new InetSocketAddress("localhost", 90),
			new InetSocketAddress("localhost", 120),
			new InetSocketAddress("localhost", 300),
			new InetSocketAddress("localhost", 1500)
		};
		index = 0;
	}
	
	@Before
	public void setup() {
		d = new Device[3];
		web = new Web(new DatabaseStub());
		d[0] = web.add(addr[0], 0);
		d[1] = web.add(addr[1], 0);
		d[2] = web.add(addr[2], 1);
		index += 3;
	}

	@Test
	public void addGet() {
		assertEquals(addr[0], web.get(d[0]));
		assertEquals(addr[1], web.get(d[1]));
		assertEquals(addr[2], web.get(d[2]));
	}

	@Test
	public void checkID() {
		assertEquals(index - 3, d[0].getID());
		assertEquals(index - 2, d[1].getID());
		assertEquals(index - 1, d[2].getID());
	}

	@Test
	public void newWebIDs() {
		web = new Web(new DatabaseStub());
		d[0] = web.add(addr[0], 0);
		assertEquals(index, d[0].getID());
		index++;
	}

	@Test
	public void checkType() {
		assertEquals(Lights.class, d[0].getClass());
		assertEquals(Lights.class, d[1].getClass());
		assertEquals(Switch.class, d[2].getClass());
	}

	@Test
	public void remove() {
		web.remove(d[1]);
		assertEquals(addr[0], web.get(d[0]));
		assertNull(web.get(d[1]));
		assertEquals(addr[2], web.get(d[2]));

		web.remove(d[0]);
		web.remove(d[2]);
		assertNull(web.get(d[0]));
		assertNull(web.get(d[1]));
		assertNull(web.get(d[2]));
	}

	@Test
	public void change1() {
		web.change(d[0], addr[3]);
		assertEquals(addr[3], web.get(d[0]));
	}

	@Test
	public void change2() {
		web.change(addr[1], addr[3]);
		assertEquals(addr[3], web.get(d[1]));
	}
	
	@Test
	public void toAString() {
		assertEquals((index-3) + ":0:0/" + (index-2) + ":0:0/" + (index-1) + ":1:0", web.toString());
	}

}
