package devices;

import types.Data;
import util.Parse;

/**
 * Created by Mega Prime 2.0 on 3/16/2017.
 */
public class Thermostat extends Device {
	int thermostatTemp = -500;
    @Override // input from device to driver 
    public void giveMessage(String msg) {
    	thermostatTemp = Parse.toInt(msg);
    }

    @Override //input to device
    public void giveInput(Data in) {
    	int temp =  Parse.toInt(in.get());
    	send(in.get());
    }

    @Override // for other drivers requests
    public Data requestOutput(Data in) {
    	if (thermostatTemp != -500)
        return new Data("temp", Parse.toString(this.thermostatTemp));
    	//if no data available
        return new Data("temp", "no temp ");
    }

    @Override //for app requests
    public String getInfo() {
    	if (thermostatTemp != -500)
            return  Parse.toString(this.thermostatTemp);
        	//if no data available
            return "no temp ";
    }
}