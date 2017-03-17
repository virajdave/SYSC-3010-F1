package devices;

import types.Data;

/**
 * Created by Mega Prime 2.0 on 3/16/2017.
 */
public class Thermostat implements Device {
    @Override
    public void giveMessage(String msg) {

    }

    @Override
    public void giveInput(Data in) {

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
