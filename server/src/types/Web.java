package types;

import java.net.InetAddress;
import java.util.HashMap;

public class Web {
	HashMap<InetAddress, Client> clients;
	
	public Web() {
		clients = new HashMap<>();
	}
	
	public Client addClient(InetAddress addr) {
		Client c = new Client();
		clients.put(addr, c);
		return c;
	}
	
	public void addDevice(InetAddress addr, int port) {
		
	}
	
	public boolean removeClient(InetAddress addr) {
		
		return true;
	}
	
	public boolean removeDevice(InetAddress addr, int port) {
		
		return true;
	}
	
	public Client getClient(InetAddress addr) {
		return clients.get(addr);
	}
	
	public Device getDevice(InetAddress addr, int port) {
		return null;
	}
}
