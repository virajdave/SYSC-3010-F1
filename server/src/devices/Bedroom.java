package devices;

import types.Data;
import util.Parse;

public class Bedroom extends Device {
	boolean lights;
	String alarm;
	@Override
	public void giveMessage(String msg) {
		String dataOut = "";
		if (msg.equals("time")) {
			
        	    dataOut = getTime();
        	    send(dataOut);
		}
		if (msg.equals("LO")){
			this.lights = true;
			
		}
		if (msg.equals("LF")){
			this.lights = false;
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
		if (in.getName().equals("alarm")){
			alarm = in.get();
		}
		return true;
	}

	@Override
	public Data requestOutput(Data in) {
		return null;
	}

	@Override
	public String getInfo() {
		if (lights){
			return "On/" + this.alarm;
		}
		else if (!lights){
			return "Off/" + this.alarm;
		}
		return "";
	}

}
