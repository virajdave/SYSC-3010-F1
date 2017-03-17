package stepDefinitions;

import static org.junit.Assert.*;
import cucumber.api.java.en.*;
import main.*;
import types.Message;
import util.Codes;
import util.Parse;

import java.net.InetSocketAddress;
import java.util.HashMap;

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

	@When("^a device named '([^']+)' of type '(\\d+)' is connected$")
	public void connectDevice(String name, String typeStr) throws Throwable {
		int type = Integer.parseInt(typeStr);

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
	public void iTurnOnLightSwitch(String set, String name) throws Throwable {
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

	@Then("^the light '([^']+)' should be turned '(on|off)'$")
	public void lightShouldBeSet(String name, String set) throws Throwable {
		Dev d = devices.get(name);
		boolean on = set.equals("on");
		server.giveMessage(new Message(Parse.toString("", Codes.W_APP, Codes.T_DEVINF, d.id), appAddr));
		
		String msg = "";
		int timeout = 0;
		while ((msg = server.getMessage(appAddr)) == null && timeout++ < TIMEOUT) {
			Thread.sleep(10);
		}
		assertEquals(Parse.toString("", Codes.W_SERVER, Codes.T_DEVINF, d.id, "/0/", on), msg);
	}

	@When("^I turn '(on|off)' the switch '([^']+)'$")
	public void iTurnOnSwitch(String set, String name) throws Throwable {
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

	@Then("^the switch '([^']+)' should be turned '(on|off)'$")
	public void switchShouldBeSet(String name, String set) throws Throwable {
		Dev d = devices.get(name);
		boolean on = set.equals("on");
		server.giveMessage(new Message(Parse.toString("", Codes.W_APP, Codes.T_DEVINF, d.id), appAddr));
		
		String msg = "";
		int timeout = 0;
		while ((msg = server.getMessage(appAddr)) == null && timeout++ < TIMEOUT) {
			Thread.sleep(10);
		}
		assertEquals(Parse.toString("", Codes.W_SERVER, Codes.T_DEVINF, d.id, "/0/", on, "/-1"), msg);
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
