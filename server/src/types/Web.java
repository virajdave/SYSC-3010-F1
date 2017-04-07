package types;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Observer;
import java.util.Set;

import devices.Device;
import util.BiMap;
import util.Database;
import util.Parse;

public class Web {
	private static int index = 0;
	
	private BiMap<InetSocketAddress, Device> devices;
	private Database db;
	
	public Web(Database database) {
		this(database, null);
	}

	public Web(Database database, Observer o) {
		devices = new BiMap<>();
		
		db = database;
		if (db.exists()) {
			// If the database already exists then restore it.
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
				Device device = Device.createNew(deviceEntry.getValue().getKey(), id, this);
				if (device != null) {
					devices.put(addr, device);
					if (o != null) {
						device.addObserver(o);
					}
				}
			}
		} else {
			// Otherwise create a fresh database from scratch.
			db.create();
		}
	}
	
	/**
	 * Get the attached database.
	 * 
	 * @return db
	 */
	public Database getDB() {
		return db;
	}
	
	/**
	 * Convert a socket address to a string.
	 * 
	 * @param addr
	 * @return String 'host:port'
	 */
	private String inetToString(InetSocketAddress addr) {
		return Parse.toString(":", addr.getHostName().toString(), addr.getPort());
	}
	
	/**
	 * Convert a string to a socket address.
	 * 
	 * @param inet String 'host:port'
	 * @return address
	 */
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
		// Create a new device of the correct type using a new ID.
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
		// Search though the devices to find the device with the given ID.
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
		// Set a new address for the given device and save it to the DB.
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
		// Get the device associated witht he old address, then set it to use new address and save it to the DB.
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
		// Remove the device entry from the DB and the device map.
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
		// Remove the device entry (by device ID) from the DB and the device map.
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
	 * Format -> id : type : dead / ...each device
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
