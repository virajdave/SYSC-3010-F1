package types;

import java.util.HashMap;

public class Client {
	HashMap<Integer, Device> devices;
	
	public Client() {
		devices = new HashMap<>();
	}
	
	public void addDevice(int port) {
		devices.put(port, new Device());
	}
	
	public Device getDevice(int port) {
		return devices.get(port);
	}
}
