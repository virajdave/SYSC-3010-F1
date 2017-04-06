package types;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import devices.Device;
import util.BiMap;
import util.Database;
import util.Parse;

public class Web {
	private static int index = 0;
	
	private BiMap<InetSocketAddress, Device> devices;
	private Database db;

	public Web() {
		devices = new BiMap<>();
		
		db = new Database("web");
		if (db.exists()) {
			HashMap<Integer, Entry<Integer, String>> data = db.getDevices();
			
			for (Entry<Integer, Entry<Integer, String>> deviceEntry : data.entrySet()) {
				// Get the ID and address.
				int id = deviceEntry.getKey();
				InetSocketAddress addr = inetFromString(deviceEntry.getValue().getValue());
				
				// Make sure the index is always greater than the instantiated IDs.
				if (id >= index) {
					index = id + 1;
				}
				
				// Create the device and add it to the web.
				Device device = Device.createNew(deviceEntry.getValue().getKey(), id, this, db.getProp(id));
				if (device != null) {
					devices.put(addr, device);
				}
			}
		} else {
			db.create();
		}
	}
	
	public Database getDB() {
		return db;
	}
	
	private String inetToString(InetSocketAddress addr) {
		return Parse.toString(":", addr.getHostName().toString(), addr.getPort());
	}
	
	private InetSocketAddress inetFromString(String inet) {
		String[] parts = inet.split(":");
		return new InetSocketAddress(parts[0], Parse.toInt(parts[1]));
	}

	/**
	 * Add a address device mapping.
	 * 
	 * @param addr InetAddress
	 * @return device
	 */
	public Device add(InetSocketAddress addr, int type) {
		int id = index++;
		Device device = Device.createNew(type, id, this);
		if (device != null) {
			// Add to the web and save an entry in the DB.
			devices.put(addr, device);
			db.addDevice(id, type, inetToString(addr));
		}
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
		db.addDevice(device.getID(), device.getType(), inetToString(new_addr));

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
		db.addDevice(device.getID(), device.getType(), inetToString(new_addr));

		return device;
	}

	/**
	 * Remove device mapping.
	 * 
	 * @param device
	 * @return if a device was removed
	 */
	public boolean remove(Device device) {
		db.removeDevice(device.getID());
		return devices.inverse().remove(device) != null;
	}

	/**
	 * Remove address mapping.
	 * 
	 * @param addr InetAddress
	 * @return if a device was removed
	 */
	public boolean remove(InetSocketAddress addr) {
		db.removeDevice(devices.get(addr).getID());
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
