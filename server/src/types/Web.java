package types;

import java.net.InetSocketAddress;
import java.util.Set;

import devices.Device;
import util.BiMap;
import util.Parse;

public class Web {
	private static int index;
	private BiMap<InetSocketAddress, Device> devices;

	public Web() {
		index = 0;
		devices = new BiMap<>();
	}

	/**
	 * Add a address device mapping.
	 * 
	 * @param addr InetAddress
	 * @return device
	 */
	public Device add(InetSocketAddress addr, int type) {
		Device device = Device.createNew(type, index++, this);
		devices.put(addr, device);
		return device;
	}

	/**
	 * Get a device by it's ID.
	 * 
	 * @param id
	 * @return device
	 */
	public Device getByID(int id) {
		for (Device d : devices.inverse().keySet()) {
			if (d.hasID(id)) {
				return d;
			}
		}
		return null;
	}
	
	/**
	 * Get the set of addresses for each device.
	 * 
	 * @return addresses
	 */
	public Set<InetSocketAddress> addrList() {
		return devices.keySet();
	}

	/**
	 * Change device -> address.
	 * 
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
	 * 
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
	 * 
	 * @param device
	 * @return if a device was removed
	 */
	public boolean remove(Device device) {
		return devices.inverse().remove(device) != null;
	}

	/**
	 * Remove address mapping.
	 * 
	 * @param addr InetAddress
	 * @return if a device was removed
	 */
	public boolean remove(InetSocketAddress addr) {
		return devices.remove(addr) != null;
	}

	/**
	 * Get address -> device.
	 * 
	 * @param addr InetAddress
	 * @return device
	 */
	public Device get(InetSocketAddress addr) {
		return devices.get(addr);
	}

	/**
	 * Get device -> Address.
	 * 
	 * @param device
	 * @return Address
	 */
	public InetSocketAddress get(Device device) {
		return devices.inverse().get(device);
	}

	/**
	 * Converts the web information to a compact string.
	 */
	@Override
	public String toString() {
		String parts[] = new String[devices.size()];
		int i = 0;

		for (Device d : devices.inverse().keySet()) {
			parts[i++] = Parse.toString(":", d.getID(), d.getType(), d.isDead());
		}

		return String.join("/", parts);
	}
}
