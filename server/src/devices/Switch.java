package devices;

import types.Data;

public class Switch extends Device {
	
	private boolean on;
	private Lights light;
	
	public Switch() {
		on = false;
		light = null;
	}
	
	private void set(boolean change) {
		if (on != change) {
			on = change;
			if (light != null) {
				light.giveInput(new Data("set", Boolean.toString(on)));
			}
		}
	}

	@Override
	public void giveMessage(String msg) {
		// Set the switch on/off.
		if(msg.equals("1")) {
			set(true);
		} else if (msg.equals("0")) {
			set(false);
		}
	}

	@Override
	public void giveInput(Data in) {
		// From app.
		if (in.is("set")) {
			set(Boolean.parseBoolean(in.get()));
		} else if (in.is("light")) {
			Device d = getDevice(Integer.parseInt(in.get()));
			if (d instanceof Lights) {
				light = (Lights)d;
			}
		}
		
	}

	@Override
	public Data requestOutput(Data in) {
		if (in.is("set")) {
			return new Data("set", Boolean.toString(on));
		}
		return null;
	}

	@Override
	public String getInfo() {
		// If the switch is on and connected light ID.
		// TODO add it
		return "";
	}

}
