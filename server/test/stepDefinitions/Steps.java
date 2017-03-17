package stepDefinitions;

import static org.junit.Assert.*;

import cucumber.api.DataTable;
import cucumber.api.java.en.*;
import main.*;
import types.Message;
import util.Codes;
import util.Parse;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Steps {
	public static final int TIMEOUT = 5;
	public static final InetSocketAddress appAddr = new InetSocketAddress("localhost", 10);

	ServerStub server;
	Manager manager;
	HashMap<String, Dev> devices;

	@Given("^the manager is started$")
	public void startManager() throws Throwable {
		server = new ServerStub();
		manager = new Manager(server);
		devices = new HashMap<>();
		manager.start();
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

		String msg = "";
		int timeout = 0;
		while ((msg = server.getMessage(addr)) == null && timeout++ < TIMEOUT) {
			Thread.sleep(10);
		}
		assertEquals(Parse.toString("", Codes.W_SERVER, Codes.T_ACK), msg.substring(0, 2));
		
		int id = Integer.parseInt(msg.substring(2));
		devices.put(name, new Dev(id, addr));
	}

	@When("^I turn '(on|off)' the (?:light|switch) '([^']+)' from the app$")
	public void iSetLightSwitch(String set, String name) throws Throwable {
		Dev d = devices.get(name);
		boolean on = set.equals("on");
		server.giveMessage(new Message(Parse.toString("", Codes.W_APP, Codes.T_DATA, d.id, "/set/", on), appAddr));
		
		String msg = "";
		int timeout = 0;
		while ((msg = server.getMessage(appAddr)) == null && timeout++ < TIMEOUT) {
			Thread.sleep(10);
		}
		assertEquals(Parse.toString("", Codes.W_SERVER, Codes.T_ACK), msg);
	}

	@When("^I turn '(on|off)' the switch '([^']+)'$")
	public void iSetSwitch(String set, String name) throws Throwable {
		Dev d = devices.get(name);
		boolean on = set.equals("on");
		server.giveMessage(new Message(Parse.toString("", Codes.W_DEVICE, Codes.T_DATA, on), d.addr));
		
		String msg = "";
		int timeout = 0;
		while ((msg = server.getMessage(d.addr)) == null && timeout++ < TIMEOUT) {
			Thread.sleep(10);
		}
		assertEquals(Parse.toString("", Codes.W_SERVER, Codes.T_ACK), msg);
	}

	@When("^I connect light '([^']+)' the switch '([^']+)'$")
	public void iConnectLightToSwitch(String lightName, String switchName) throws Throwable {
		Dev d = devices.get(switchName);
		int id = devices.get(lightName).id;
		server.giveMessage(new Message(Parse.toString("", Codes.W_APP, Codes.T_DATA, d.id, "/light/", id), appAddr));

		String msg = "";
		int timeout = 0;
		while ((msg = server.getMessage(appAddr)) == null && timeout++ < TIMEOUT) {
			Thread.sleep(10);
		}
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
		
		String msg = "";
		int timeout = 0;
		while ((msg = server.getMessage(appAddr)) == null && timeout++ < TIMEOUT) {
			Thread.sleep(10);
		}
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
		
		String msg = "";
		int timeout = 0;
		while ((msg = server.getMessage(appAddr)) == null && timeout++ < TIMEOUT) {
			Thread.sleep(10);
		}
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
	
	public class Dev {
		public int id;
		public InetSocketAddress addr;
		
		public Dev(int id, InetSocketAddress addr) {
			this.id = id;
			this.addr = addr;
		}
	}
}
