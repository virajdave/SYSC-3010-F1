package devices;

import java.util.Observable;

import types.Conf;
import types.Data;
import types.Info;

public abstract class Device extends Observable {
	private int id;
	
	public abstract void giveMessage(String msg);
	
	public abstract boolean giveInput(Data in);
	
	public abstract boolean setConf(Conf in);
	
	public abstract boolean requestOutput(Data in);
	
	public abstract Info getInfo();
	
	public int getID() {
		return id;
	}
	
	public boolean hasID(int i) {
		return id == i;
	}
	
	public static Device createNew(int type, int id) {
		Device d = null;
		try {
			d = Device.types[type].newInstance();
			d.id = id;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return d;
	}
	
	@SuppressWarnings("unchecked")
	public static Class<? extends Device>[] types = new Class[] {
		Lights.class,
		Switch.class
	};
}
