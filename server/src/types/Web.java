package types;

import java.net.InetSocketAddress;

import devices.Device;
import util.BiMap;

public class Web {
	private static int index;
	private BiMap<InetSocketAddress, Device> devices;
	
	public Web() {
		index = 0;
		devices = new BiMap<>();
	}
	
	/**
	 * Add a address device mapping.
	 * @param addr InetAddress
	 * @return device
	 */
	public Device add(InetSocketAddress addr, int type) {
		Device device = Device.createNew(type, index++);
		devices.put(addr, device);
		return device;
	}
	
	/**
	 * Change device -> address.
	 * @param device
	 * @param new_addr address to change to
	 * @return device
	 */
	public Device change(Device device, InetSocketAddress new_addr) {
		devices.inverse().put(device, new_addr);
		
		return device;
	}
	
	/**
	 * Change address - address.
	 * @param old_addr address to change from
	 * @param new_addr address to change to
	 * @return device
	 */
	public Device change(InetSocketAddress old_addr, InetSocketAddress new_addr) {
		Device device = devices.get(old_addr);
		devices.inverse().put(device, new_addr);
		
		return device;
	}
	
	/**
	 * Remove device mapping.
	 * @param device
	 * @return if a device was removed
	 */
	public boolean remove(Device device) {
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
	 * Get address -> device.
	 * @param addr InetAddress
	 * @return device
	 */
	public Device get(InetSocketAddress addr) {
		return devices.get(addr);
	}
	
	/**
	 * Get device -> Address.
	 * @param device
	 * @return Address
	 */
	public InetSocketAddress get(Device device) {
		return devices.inverse().get(device);
	}
}
