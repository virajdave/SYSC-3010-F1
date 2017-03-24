package stepDefinitions;

import static org.junit.Assert.*;

import cucumber.api.DataTable;
import cucumber.api.java.en.*;
import main.*;
import types.Message;
import util.Codes;
import util.Parse;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Steps {
	public static final InetSocketAddress appAddr = new InetSocketAddress("localhost", 10);

	ServerStub server;
	Manager manager;
	HashMap<String, Dev> devices;

	@Given("^the manager is started$")
	public void startManager() throws Throwable {
		server = new ServerStub();
		manager = new Manager(server, 0, 1);
		devices = new HashMap<>();
		manager.start();

		// Sleep to allow threads to start up and get going.
		Thread.sleep(5);
	}

	@Given("^the manager sends a heartbeat$")
	public void sendHearbeat() throws Throwable {
		manager.doHeartBeat();
		Thread.sleep(1);
	}

	@Given("^the manager heartbeat times out$")
	public void endHearbeat() throws Throwable {
		Thread.sleep(devices.size() * 5);
	}

	@When("^a '([^']+)' named '([^']+)' is connected$")
	public void connectDevice(String typeStr, String name) throws Throwable {
		int type = -1;
		if (typeStr.equals("light")) {
			type = 0;
		} else if (typeStr.equals("switch")) {
			type = 1;
		} else if (typeStr.equals("mirror")) {
			type = 2;
		} else if (typeStr.equals("thermostat")) {
			type = 3;
		} else if (typeStr.equals("bed")) {
			type = 4;
		} else {
			fail("Unknown device type to connect.");
		}

		InetSocketAddress addr = server.giveMessageNewAddr(Parse.toString("", Codes.W_DEVICE, Codes.T_BEAT, type));

		String msg = server.getMessage(addr);
		assertEquals(Parse.toString("", Codes.W_SERVER, Codes.T_ACK), msg.substring(0, 2));

		int id = Integer.parseInt(msg.substring(2));
		devices.put(name, new Dev(id, type, addr));
	}

	@When("^I turn '(on|off)' the (?:light|switch) '([^']+)' from the app$")
	public void iSetLightSwitch(String set, String name) throws Throwable {
		Dev d = devices.get(name);
		boolean on = set.equals("on");
		server.giveMessage(new Message(Parse.toString("", Codes.W_APP, Codes.T_DATA, d.id, "/set/", on), appAddr));

		String msg = server.getMessage(appAddr);
		assertEquals(Parse.toString("", Codes.W_SERVER, Codes.T_ACK), msg);
	}

	@When("^I turn '(on|off)' the switch '([^']+)'$")
	public void iSetSwitch(String set, String name) throws Throwable {
		Dev d = devices.get(name);
		boolean on = set.equals("on");
		server.giveMessage(new Message(Parse.toString("", Codes.W_DEVICE, Codes.T_DATA, on), d.addr));

		String msg = server.getMessage(d.addr);
		assertEquals(Parse.toString("", Codes.W_SERVER, Codes.T_ACK), msg);
	}

	@When("^I connect light '([^']+)' the switch '([^']+)'$")
	public void iConnectLightToSwitch(String lightName, String switchName) throws Throwable {
		Dev d = devices.get(switchName);
		int id = devices.get(lightName).id;
		server.giveMessage(new Message(Parse.toString("", Codes.W_APP, Codes.T_DATA, d.id, "/light/", id), appAddr));

		String msg = server.getMessage(appAddr);
		assertEquals(Parse.toString("", Codes.W_SERVER, Codes.T_ACK), msg);
	}

	@Then("^the light '([^']+)' should be turned '(on|off)'$")
	public void lightShouldBeSet(String name, String set) throws Throwable {
		List<List<String>> data = new LinkedList<>();
		data.add(new LinkedList<>());
		data.get(0).add("set");
		data.get(0).add(set);
		lightShouldBe(name, DataTable.create(data));
	}

	@Then("^the switch '([^']+)' should be turned '(on|off)'$")
	public void switchShouldBeSet(String name, String set) throws Throwable {
		List<List<String>> data = new LinkedList<>();
		data.add(new LinkedList<>());
		data.get(0).add("set");
		data.get(0).add(set);
		switchShouldBe(name, DataTable.create(data));
	}

	@Then("^the light '([^']+)' should be:$")
	public void lightShouldBe(String name, DataTable dataTable) throws Throwable {
		Dev d = devices.get(name);
		server.giveMessage(new Message(Parse.toString("", Codes.W_APP, Codes.T_DEVINF, d.id), appAddr));

		String msg = server.getMessage(appAddr);
		String code = msg.substring(0, 2);
		String[] split = msg.substring(2).split("/");
		assertEquals(Parse.toString("", Codes.W_SERVER, Codes.T_DEVINF), code);
		assertEquals(3, split.length);
		assertEquals(Parse.toString(d.id), split[0]);

		List<List<String>> data = dataTable.raw();
		for (List<String> row : data) {
			if (row.get(0).equals("dead")) {
				boolean dead = row.get(1).equals("true");
				assertEquals(Parse.toString(dead), split[1]);
			} else if (row.get(0).equals("set")) {
				boolean on = row.get(1).equals("on");
				assertEquals(Parse.toString(on), split[2]);
			} else {
				fail("Unknown info row.");
			}
		}
	}

	@Then("^the switch '([^']+)' should be:$")
	public void switchShouldBe(String name, DataTable dataTable) throws Throwable {
		Dev d = devices.get(name);
		server.giveMessage(new Message(Parse.toString("", Codes.W_APP, Codes.T_DEVINF, d.id), appAddr));

		String msg = server.getMessage(appAddr);
		String code = msg.substring(0, 2);
		String[] split = msg.substring(2).split("/");
		assertEquals(Parse.toString("", Codes.W_SERVER, Codes.T_DEVINF), code);
		assertEquals(4, split.length);
		assertEquals(Parse.toString(d.id), split[0]);

		List<List<String>> data = dataTable.raw();
		for (List<String> row : data) {
			if (row.get(0).equals("dead")) {
				boolean dead = row.get(1).equals("true");
				assertEquals(Parse.toString(dead), split[1]);
			} else if (row.get(0).equals("set")) {
				boolean on = row.get(1).equals("on");
				assertEquals(Parse.toString(on), split[2]);
			} else if (row.get(0).equals("light")) {
				if (row.get(1).equals("null")) {
					assertEquals("-1", split[3]);
				} else {
					int id = devices.get(row.get(1)).id;
					assertEquals(Parse.toString(id), split[3]);
				}
			} else {
				fail("Unknown info row.");
			}
		}
	}

	@Then("^I should see the following list of devices in the app:$")
	public void netInfo(DataTable dataTable) throws Throwable {
		server.giveMessage(new Message(Parse.toString("", Codes.W_APP, Codes.T_NETINF), appAddr));

		String msg = server.getMessage(appAddr);
		String code = msg.substring(0, 2);
		assertEquals(Parse.toString("", Codes.W_SERVER, Codes.T_NETINF), code);

		// Split apart the info to use.
		String[] split = msg.substring(2).split("/");
		HashMap<Integer, String[]> info = new HashMap<>();
		for (String i : split) {
			String[] s = i.split(":");
			info.put(Parse.toInt(s[0]), Arrays.copyOfRange(s, 1, s.length));
		}
		assertEquals("info must have duplicate IDs if hashmap size didn't match", info.size(), split.length);

		List<List<String>> data = dataTable.raw();
		for (List<String> row : data) {
			Dev d = devices.get(row.get(0));
			
			String[] s = info.get(d.id);
			assertNotNull("Device '" + row.get(0)  + "' was not in the network list", s);
			assertEquals(Parse.toString(d.type), s[0]);
			if (row.size() > 1) {
				assertEquals(row.get(1).equalsIgnoreCase("true") ? "1" : "0", s[1]);
			}
		}
	}

	public class Dev {
		public int id, type;
		public InetSocketAddress addr;

		public Dev(int id, int type, InetSocketAddress addr) {
			this.id = id;
			this.type = type;
			this.addr = addr;
		}
	}
}
