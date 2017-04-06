package devices;

import types.Data;
import util.Parse;

public class Bedroom extends Device {
	boolean lights;
	@Override
	public void giveMessage(String msg) {
		if (msg.equals("time")) {
        	    dataOut = getTime();
		}
	}
	public String getTime() {
        	long time = System.currentTimeMillis();
        	return "t"+ time;
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
