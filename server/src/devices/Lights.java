package devices;

import java.util.HashMap;

import types.Data;
import util.Parse;

public class Lights extends Device {

	private boolean on;

	public Lights() {
		on = false;
	}
	
	public Lights(HashMap<String, String> data) {
		// Use the DB property if it has been set.
		on = data.containsKey("on") ? Parse.toBool(data.get("on")) : false;
	}

	private void set(boolean change) {
		// Toggle the light on or off if it is changing.
		if (on != change) {
			on = change;
			if (on) {
				send("1");
			} else {
				send("0");
			}
			
			// Save the new value to the DB.
			setProperty("on", Parse.toString(on));
		}
	}

	@Override
	public void giveMessage(String msg) {
		// Nothing here.
	}

	@Override
	public boolean giveInput(Data in) {
		if (in.is("set")) {
			// Set the light on or off.
			set(Parse.toBool(in.get()));
		} else {
			return false;
		}
		return true;
	}

	@Override
	public Data requestOutput(Data in) {
		if (in.is("set")) {
			// Return if the light is on or off.
			return new Data("set", Parse.toString(on));
		}
		return null;
	}

	@Override
	public String getInfo() {
		// 'id / dead / light on'.
		return Parse.toString("/", this.getID(), this.isDead(), on);
	}

}
