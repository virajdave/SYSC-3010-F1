package devices;

import types.Data;
import util.Parse;

/**
 * Created by Mega Prime 2.0 on 3/16/2017.
 */
public class Thermostat extends Device {
	float thermostatTemp = -500;
    @Override // input from device to driver 
    public void giveMessage(String msg) {
    	thermostatTemp = Parse.toFloat(msg);
    }

    @Override //input to device
    public boolean giveInput(Data in) {
    	int temp =  Parse.toInt(in.get());
    	send(in.get());
    	return true;
    }

    @Override // for other drivers requests
    public Data requestOutput(Data in) {
    	if (in.is("temp")) {
    		if (thermostatTemp != -500)
    			return new Data("temp", Parse.toString(this.thermostatTemp));
    	}
		//if no data available
    	return null;
    }

    @Override //for app requests
    public String getInfo() {
    	if (thermostatTemp != -500)
            return  Parse.toString(this.thermostatTemp);
        	//if no data available
            return "no temp ";
    }
}
