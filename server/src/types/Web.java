package types;

import java.net.InetAddress;
import java.util.HashMap;

public class Web {
	private int index;
	private HashMap<InetAddress, Integer> devices;
	
	public Web() {
		index = 0;
		devices = new HashMap<>();
	}
	
	/**
	 * Get an address device ID mapping.
	 * @param addr InetAddress
	 * @return device ID
	 */
	public Integer add(InetAddress addr) {
		int device = index++;
		devices.put(addr, device);
		return device;
	}
	
	/**
	 * Remove an address device ID mapping.
	 * @param addr InetAddress
	 * @return if a device was removed
	 */
	public boolean remove(InetAddress addr) {
		return devices.remove(addr) != null;
	}
	
	/**
	 * Get an address device ID mapping.
	 * @param addr InetAddress
	 * @return device ID
	 */
	public Integer get(InetAddress addr) {
		return devices.get(addr);
	}
}
