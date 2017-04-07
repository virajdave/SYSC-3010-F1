package devices;

import java.util.HashMap;

import types.Data;
import util.Parse;

public class Thermostat extends Device {	
	private Float setTemp;
	private Float thermostatTemp;
	
	public Thermostat() {
		setTemp        = null;
		thermostatTemp = null;
	}
	
	public Thermostat(HashMap<String, String> data) {
		setTemp        = data.containsKey("set Temp") ? Parse.toFloat(data.get("set Temp")) : null;
		thermostatTemp = data.containsKey("currTemp") ? Parse.toFloat(data.get("currTemp")) : null;
	}
	
    @Override // input from device to driver 
    public void giveMessage(String msg) {
    	thermostatTemp = Parse.toFloat(msg);
    	
    	setProperty("currTemp", msg);
    }

    @Override //input to device
    public boolean giveInput(Data in) {
    	setTemp = Parse.toFloat(in.get());
    	setProperty("set Temp", in.get());
    	send(in.get());
    	
    	return true;
    }

    @Override // for other drivers requests
    public Data requestOutput(Data in) {
    	if (in.is("temp")) {
    		if (thermostatTemp != null)
    			return new Data("temp", Parse.toString(thermostatTemp));
    	}
		//if no data available
    	return null;
    }

    @Override //for app requests
    public String getInfo() {
    	if (thermostatTemp != null)
            return Parse.toString(thermostatTemp);
    	//if no data available
        return "no temp ";
    }
}
