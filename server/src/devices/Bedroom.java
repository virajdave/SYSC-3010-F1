package devices;

import types.Data;
import util.Parse;

public class Bedroom extends Device {
	boolean lights;
	@Override
	public void giveMessage(String msg) {
		
	}

	@Override
	public boolean giveInput(Data in) {
		send(in.getName() + "/" + in.get());
		if (in.getName().equals("l")){
			lights = Parse.toBool(in.get());
		} else {
			return false;
		}
		return true;
	}

	@Override
	public Data requestOutput(Data in) {
		return null;
	}

	@Override
	public String getInfo() {
		return null;
	}

}
