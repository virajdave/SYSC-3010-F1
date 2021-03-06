package devices;

import types.Data;
import util.Parse;

public class Switch extends Device {

	private boolean on;
	private Device light;

	public Switch() {
		on = false;
		light = null;
	}

	private void set(boolean change) {
		if (on != change) {
			on = change;

			if (light != null) {
				light.giveInput(new Data("set", Parse.toString(on)));
			}
		}
	}

	@Override
	public void giveMessage(String msg) {
		// Set the switch on/off.
		try {
			set(Parse.toBool(msg));
		} catch (IllegalArgumentException e) {
		}
	}

	@Override
	public boolean giveInput(Data in) {
		// From app.
		if (in.is("set")) {
			set(Parse.toBool(in.get()));
		} else if (in.is("light")) {
			Device d = getDevice(Parse.toInt(in.get()));
			if (d != null) {
				light = d;
				updateFromLight();
			}
		} else {
			return false;
		}
		return true;
	}

	@Override
	public Data requestOutput(Data in) {
		if (in.is("set")) {
			return new Data("set", Parse.toString(on));
		}
		return null;
	}

	private void updateFromLight() {
		Data isSet = light.requestOutput(new Data("set"));
		if (isSet != null) {
			on = Parse.toBool(isSet.get());
		}
	}

	@Override
	public String getInfo() {
		// If the switch is on and connected light ID.
		if (light != null) {
			updateFromLight();
			return Parse.toString("/", this.getID(), this.isDead(), on, light.getID());
		} else {
			return Parse.toString("/", this.getID(), this.isDead(), on, -1);
		}
	}

}
