package devices;

import java.net.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import types.Data;

public class Mirror extends Device {
    private Thermostat thermo;
    private String currentColour;

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
        this();
        this.thermo = t;
    }

    /**
     * Creates the JSON string with the time
     * @return JSON string of time
     */
    public String getTime() {
        long time = System.currentTimeMillis();
        return "\"time\":{"+ time + "}";
    }


    public String getWeather() throws IOException {
        String url = buildWeatherURL("Ottawa,Ca");
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return jsonText;
        } finally {
            is.close();
        }
    }

    /**
     * Reads all the bits in from a bufferedreader
     * @param rd
     * @return String with all the info read in
     * @throws IOException
     */
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private String buildWeatherURL(String city) {
        String user_api = 'b86f030e92681cb37afdbb0f336668ae'
        String unit = 'metric'  // For Fahrenheit use imperial, for Celsius use metric, and the default is Kelvin.
        String api = 'http://api.openweathermap.org/data/2.5/weather?q='
        String full_api_url = api + city + '&mode=json&units=' + unit + '&APPID=' + user_api
        return full_api_url
    }

    public String thermoTemp() {
        return "";
    }

    public String setColour() {
        return "";
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