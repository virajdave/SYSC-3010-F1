package devices;

import types.Data;
import util.Log;

public class Null extends Device {

	@Override
	public void giveMessage(String msg) {
		Log.out("Null device #" + getID() + " with type " + getType() + " was given msg '" + msg + "'");
	}

	@Override
	public void giveInput(Data in) {
		Log.out("Null device #" + getID() + " with type " + getType() + " was given input '" + in.getName() + "/" + in.get() + "'");
	}

	@Override
	public Data requestOutput(Data in) {
		Log.out("Null device #" + getID() + " with type " + getType() + " was requested output '" + in.getName() + "'");
		return null;
	}

	@Override
	public String getInfo() {
		Log.out("Null device #" + getID() + " with type " + getType() + " was asked for info");
		return null;
	}

}
