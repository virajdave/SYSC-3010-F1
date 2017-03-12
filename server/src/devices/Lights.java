package devices;

import types.Data;
import util.Parse;

public class Lights extends Device {
	
	private boolean on;
	
	public Lights() {
		on = false;
	}
	
	private void set(boolean change) {
		if (on != change) {
			on = change;
			if (on) {
			    send("1");
			} else {
				send("0");
			}
		}
	}

	@Override
	public void giveMessage(String msg) {
		// Nothing here.
	}

	@Override
	public void giveInput(Data in) {
		if (in.is("set")) {
			set(Parse.toBool(in.get()));
		}
		
	}

	@Override
	public Data requestOutput(Data in) {
		if (in.is("set")) {
			return new Data("set", Parse.toString(on));
		}
		return null;
	}

	@Override
	public String getInfo() {
		// If the lights are on.
		return Parse.toString("/", this.getID(), this.isDead(), on);
	}

}
