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
	 * Restore a new device of the giving type using the data.
	 * @param type
	 * @param id
	 * @param data
	 * @return
	 */
	public static Device createNew(int type, int id, Web web, HashMap<String, String> data) {
		Device d = null;
		
		try {
			// If out of range add a null device.
			if (type > types.length - 2 || type < 0) {
				Log.warn("Device #" + id + " type '" + type + "' is out of range, adding Null device.");
				d = types[types.length - 1].newInstance();
				
				d.type = type;
				d.id = id;
				d.web = web;
			} else {
				if (data == null) {
					d = types[type].newInstance();
				} else {
					boolean hasConstructor = false;
					for (Constructor<?> constructor : types[type].getConstructors()) {
						Type[] parameterTypes = constructor.getGenericParameterTypes();
						if (parameterTypes.length == 1) {
							hasConstructor = true;
							break;
						}
					}
					if (hasConstructor) {
						d = types[type].getConstructor(data.getClass()).newInstance(data);
					} else {
						d = types[type].newInstance();
					}
				}
				
				d.type = type;
				d.id = id;
				d.web = web;
			}
		} catch (Exception e) {
			Log.err("Exception when creating device", e);
		}
		
		return d;
	}
	
	/**
	 * Create a new device of the giving type.
	 * @param type
	 * @param id
	 * @return
	 */
	public static Device createNew(int type, int id, Web web) {
		return createNew(type, id, web, null);
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
