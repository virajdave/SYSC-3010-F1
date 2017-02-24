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
	 * Add a address device ID mapping.
	 * @param addr InetAddress
	 * @return device ID
	 */
	public Integer add(InetSocketAddress addr) {
		int device = index++;
		devices.put(addr, device);
		return device;
	}
	
	/**
	 * Change device ID -> address.
	 * @param device ID
	 * @param new_addr address to change to
	 * @return device ID
	 */
	public Integer change(Integer device, InetSocketAddress new_addr) {
		devices.inverse().put(device, new_addr);
		
		return device;
	}
	
	/**
	 * Change address - address.
	 * @param old_addr address to change from
	 * @param new_addr address to change to
	 * @return device ID
	 */
	public Integer change(InetSocketAddress old_addr, InetSocketAddress new_addr) {
		Integer device = devices.get(old_addr);
		devices.inverse().put(device, new_addr);
		
		return device;
	}
	
	/**
	 * Remove device ID mapping.
	 * @param device ID
	 * @return if a device was removed
	 */
	public boolean remove(Integer device) {
		return devices.inverse().remove(device) != null;
	}
	
	/**
	 * Remove address mapping.
	 * @param addr InetAddress
	 * @return if a device was removed
	 */
	public boolean remove(InetSocketAddress addr) {
		return devices.remove(addr) != null;
	}
	
	/**
	 * Get address -> device ID.
	 * @param addr InetAddress
	 * @return device ID
	 */
	public Integer get(InetSocketAddress addr) {
		return devices.get(addr);
	}
	
	/**
	 * Get device ID -> Address.
	 * @param device ID
	 * @return Address
	 */
	public InetSocketAddress get(Integer device) {
		return devices.inverse().get(device);
	}
}
