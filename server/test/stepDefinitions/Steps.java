package stepDefinitions;

import static org.junit.Assert.*;
import cucumber.api.java.en.*;
import main.*;
import util.Codes;
import util.Parse;

import java.net.InetSocketAddress;
import java.util.HashMap;

public class Steps {
	ServerStub server;
	Manager manager;
	HashMap<String, InetSocketAddress> devices;

	@Given("^the manager is started$")
	public void startManager() throws Throwable {
		server = new ServerStub();
		manager = new Manager(server);
		devices = new HashMap<>();
		manager.start();
	}

	@When("^a device named '([^']+)' of type '(\\d+)' is connected$")
	public void connectDevice(String name, String idStr) throws Throwable {
		int id = Integer.parseInt(idStr);

		InetSocketAddress addr = server.giveMessageNewAddr(Parse.toString("", Codes.W_DEVICE, Codes.T_BEAT, id));
		devices.put(name, addr);
		
		String ack = "";
		int timeout = 0;
		while ((ack = server.getMessage(addr)) == null && timeout++ < 50) {
			Thread.sleep(10);
		}
		assertEquals(Parse.toString("", Codes.W_SERVER, Codes.T_ACK), ack);
	}
}
