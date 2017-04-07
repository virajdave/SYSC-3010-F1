package devices;

import java.util.HashMap;

import types.Data;
import util.Parse;

public class Bedroom extends Device {
	private boolean lights;
	private String alarm;
	
	public Bedroom() {
		lights = false;
		alarm = "";
	}
	
	public Bedroom(HashMap<String, String> data) {
		lights = data.containsKey("lights") ? Parse.toBool(data.get("lights")) : false;
		alarm  = data.containsKey("alarm")  ? data.get("alarm")                : "";
	}
	
	@Override
	public void giveMessage(String msg) {		
		if (msg.equals("time")) {
			// Asking for current time.
			send(getTime());
		} else

		if (msg.equals("LO")){
			// Lights switched on.
			lights = true;
			setProperty("lights", Parse.toString(lights));
		} else

		if (msg.equals("LF")) {
			// Lights switched off.
			lights = false;
			setProperty("lights", Parse.toString(lights));
		}
	}
	
	private String getTime() {
        long time = System.currentTimeMillis();
        return "t"+ time;
    }
	
	@Override
	public boolean giveInput(Data in) {
		if (in.getName().equals("l")) {
			// Set lights on/off.
			lights = Parse.toBool(in.get());
			send(Parse.toString("/", in.getName(), in.get()));
			setProperty("lights", in.get());
		} else
		
		if (in.getName().equals("alarm")) {
			// Set alarm time.
			String s = in.get();
			if (s.length() == 5) {
				try {
					// Make sure the time is in the correct format.
					int hour = Parse.toInt(s.substring(0, 1));
					int minute = Parse.toInt(s.substring(3, 4));
					if (hour < 24 && hour >= 0 && minute < 60 && minute >= 0) {
						alarm = s;
						send(Parse.toString("/", "alarm", alarm));
						setProperty("alarm", alarm);
						return true;
					}
				} catch (NumberFormatException e) {
					// Can be ignored, simply return false.
				}
				return false;
			}
		} else
			
		return false;
		return true;
	}

	@Override
	public Data requestOutput(Data in) {
		return null;
	}

	@Override
	public String getInfo() {
		return Parse.toString("/", lights, alarm);
	}

}
