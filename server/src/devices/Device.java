package devices;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Observable;

import types.Data;
import types.Web;
import util.Log;

public abstract class Device extends Observable {
	private int type = -1;
	private int id = -1;
	private boolean dead = false;
	private Web web;
	
	/**
	 * Give a message to this driver from it's device.
	 * @param msg
	 */
	public abstract void giveMessage(String msg);
	
	/**
	 * Give a data input from another device.
	 * @param in
	 */
	public abstract boolean giveInput(Data in);
	
	
	/**
	 * Honor a request for a data output from another device.
	 * @param in
	 * @return
	 */
	public abstract Data requestOutput(Data in);
	
	/**
	 * Information specific to this device, used by the app.
	 * @return
	 */
	public abstract String getInfo();
	
	protected void send(String message) {
	    setChanged();
	    notifyObservers(message);
	}
	
	protected Device getDevice(int id) {
		return web.getByID(id);
	}
	
	protected String getProperty(String key) {
		return web.getDB().getProp(this.getID(), key);
	}
	
	protected HashMap<String, String> getProperties() {
		return web.getDB().getProp(this.getID());
	}
	
	protected boolean setProperty(String key, String data) {
		return web.getDB().addProp(this.getID(), key, data);
	}
	
	/**
	 * Return the device type.
	 * @return
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Return the unique device ID.
	 * @return
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Check if this device has the specified ID.
	 * @param i
	 * @return
	 */
	public boolean hasID(int i) {
		return id == i;
	}
	
	/**
	 * Is this device dead/disconnected.
	 * @return
	 */
	public boolean isDead() {
		return dead;
	}
	
	/**
	 * Set if this device is dead/disconnected.
	 * @param dead
	 */
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	/**
	 * Create a new device of the giving type.
	 * @param type
	 * @param id
	 * @param data
	 * @return
	 */
	public static Device createNew(int type, int id, Web web) {
		Device d = null;
		
		try {
			// If out of range add a null device.
			if (type > types.length - 2 || type < 0) {
				Log.warn("Device #" + id + " type '" + type + "' is out of range, adding Null device.");
				d = types[types.length - 1].newInstance();
			} else {
				// Get the properties related to this device.
				HashMap<String, String> data = web.getDB().getProp(id);
				boolean useImport = false;
				
				// If there is data and the driver has the required constructor use import.
				if (data != null) {
					for (Constructor<?> constructor : types[type].getConstructors()) {
						Type[] parameterTypes = constructor.getGenericParameterTypes();
						if (parameterTypes.length == 1) {
							useImport = true;
							break;
						}
					}
				}
				
				// If importing pass in the data to the correct constructor.
				if (useImport) {
					d = types[type].getConstructor(data.getClass()).newInstance(data);
				} else {
					d = types[type].newInstance();
				}
			}

			// Set variables attached to the driver.
			d.type = type;
			d.id = id;
			d.web = web;
		} catch (Exception e) {
			Log.err("Exception when creating device", e);
		}
		
		return d;
	}
	
	@SuppressWarnings("unchecked")
	public static Class<? extends Device>[] types = new Class[] {
		Lights.class,
		Switch.class,
		Mirror.class,
		Thermostat.class,
		Bedroom.class,
		Null.class
	};
}
