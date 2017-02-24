package types;

import java.net.InetSocketAddress;
import util.BiMap;

public class Web {
	private int index;
	private BiMap<InetSocketAddress, Integer> devices;
	
	public Web() {
		index = 0;
		devices = new BiMap<>();
	}
	
	/**
	 * Get an address device ID mapping.
	 * @param addr InetAddress
	 * @return device ID
	 */
	public Integer add(InetSocketAddress addr) {
		int device = index++;
		devices.put(addr, device);
		return device;
	}
	
	/**
	 * Change an address device ID mapping.
	 * @param addr InetAddress
	 * @return if a device was removed
	 */
	public Integer change(Integer device, InetSocketAddress new_addr) {
		devices.inverse().put(device, new_addr);
		
		return device;
	}
	
	/**
	 * Change an address device ID mapping.
	 * @param addr InetAddress
	 * @return if a device was removed
	 */
	public Integer change(InetSocketAddress old_addr, InetSocketAddress new_addr) {
		Integer device = devices.get(old_addr);
		devices.inverse().put(device, new_addr);
		
		return device;
	}
	
	/**
	 * Remove an address device ID mapping.
	 * @param addr InetAddress
	 * @return if a device was removed
	 */
	public boolean remove(InetSocketAddress addr) {
		return devices.remove(addr) != null;
	}
	
	/**
	 * Get an address device ID mapping.
	 * @param addr InetAddress
	 * @return device ID
	 */
	public Integer get(InetSocketAddress addr) {
		return devices.get(addr);
	}
}
