package devices;

import java.net.*;
import types.Data;

public class Mirror implements Device {
    Thermostat thermo;
    String currentColour;

    /**
     * create blank mirror driver
     */
    public Mirror () {
        currentColour = "#2E99A9";
    }

    /**
     * Create Mirror driver with a thermostat
     * @param t
     */
    public Mirror(Thermostat t) {
        this.thermo = t;
        this.Mirror();
    }


    public String getTime() {
        long time = System.currentTimeMillis();
        return "\"time\":{"+ time + "}";
    }

    public String getWeather() {

    }

    public String thermoTemp() {

    }

    public String setColour() {

    }

    public void setLoc() {

    }

    public void setThermoDevice() {

    }

    @java.lang.Override
    public void giveMessage(String msg) {

    }

    @java.lang.Override
    public void giveInput(Data in) {

    }

    @java.lang.Override
    public Data requestOutput(Data in) {
        return null;
    }

    @java.lang.Override
    public String getInfo() {
        return null;
    }
}