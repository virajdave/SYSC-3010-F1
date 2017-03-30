package devices;

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
	public abstract void giveInput(Data in);
	
	
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
	 * @return
	 */
	public static Device createNew(int type, int id, Web web) {
		Device d = null;
		try {
			d = types[type].newInstance();
			d.type = type;
			d.id = id;
			d.web = web;
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.warn("Device #" + id + " type '" + type + "' is out of range.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return d;
	}
	
	@SuppressWarnings("unchecked")
	public static Class<? extends Device>[] types = new Class[] {
		Lights.class,
		Switch.class,
		Mirror.class,
		Thermostat.class,
		Bedroom.class
	};
}
