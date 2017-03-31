package devices;

import java.net.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import types.Data;

public class Mirror extends Device {
    private Thermostat thermo;
    private String currentColour;
    private String currStop;
    private String currRoute;
    private String currDirection;
    private String lon;
    private String lat;

    /**
     * create blank mirror driver
     */
    public Mirror () {
        currentColour = "#2E99A9";
        currStop = "3031";
        currRoute = "104";
        currDirection = "0";
        lon = "-75.6981200";
        lat = "45.4111700";
    }


    /**
     * Creates the JSON string with the time
     * @return JSON string of time
     */
    public String getTime() {
        long time = System.currentTimeMillis();
        return "t"+ time;
    }

    public void setLoc() {

    }
    
    public void changeRoute(String routeInfo) {
    	String[] temp;
        String delimeter = ",";
        temp = routeInfo.split(delimeter);
    	setStation(temp[0]);
    	setRoute(temp[1]);
    	setDirection(temp[2]);
    	try {
			String dataOut = getBus();
			send("d" + this.currDirection);
			send(dataOut);	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void setStation (String station) {
    	this.currStop = station;
    }
    
    private void setRoute (String route) {
    	this.currRoute = route;
    }
    
    private void setDirection (String direction) {
    	this.currDirection = direction;
    }
    
    public String getStation () {
    	return this.currStop;
    }
    
    public String getRoute () {
    	return this.currRoute;
    }
    
    public String getDirection () {
    	return this.currDirection;
    }
    
    public String getBus() throws IOException {
        String url = buildBusURL(currStop, currRoute);
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            return 'b'+ readAll(rd);
        } finally {
            is.close();
        }
    }
    
    private String buildBusURL(String stop, String route) {
    	String key = "appID=7f6091d8&apiKey=4be816c142bfb4100421b9cbdef4fb9a";
    	String strStop = "&stopNo=" + stop;
    	String strRoute = "&routeNo=" + route;
    	String url = "https://api.octranspo1.com/v1.2/GetNextTripsForStop?";
    	String full_api_url = url + key + strRoute + strStop + "&format=json";
        return full_api_url;
    }
    
    
    public String getWeather() throws IOException {
        String url = buildWeatherURL();
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            return 'w'+ readAll(rd);
        } finally {
            is.close();
        }
    }
    
    private String buildWeatherURL() {
        String user_api = "b86f030e92681cb37afdbb0f336668ae";
        String unit = "metric";  // For Fahrenheit use imperial, for Celsius use metric, and the default is Kelvin.
        String api = "http://api.openweathermap.org/data/2.5/weather?lat=";
        
        String full_api_url = api + lat + "&lon=" + lon + "&mode=json&units=" + unit + "&APPID=" + user_api;
        return full_api_url;
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

    private void setColour(String colour) {
    	// Check the new colour matches the required pattern.
        String HEX_PATTERN = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        Pattern r = Pattern.compile(HEX_PATTERN);
        Matcher m = r.matcher(colour);
        if (m.find( )) {
        	// Set the new colour.
            this.currentColour = m.group(0);
        }
    }
    
    private String getColour() {
    	return this.currentColour;
    }

    public String thermoTemp() {
        return "";
    }
    
    public void setThermoDevice() {

    }

    @Override
    public void giveMessage(String msg) {
        String dataOut = "";
        if (msg.equals("weather")) {
            try {
				dataOut = getWeather();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else if (msg.equals("time")) {
            dataOut = getTime();
        } else if (msg.equals("bus")) {
        	try {
				dataOut = getBus();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        send(dataOut);
    }

    @Override
    public void giveInput(Data in) {
    	if (in.is("colour")) {
    		// Set the new colour and then send it out.
    		this.setColour(in.get());
    		send("c" + currentColour);
    	} else if (in.is("route")) {
    		this.changeRoute(in.get());
    	}
    }

    @Override
    public Data requestOutput(Data in) {
    	Data dataOut = null;
    	if (in.is("colour")) {
    		dataOut = new Data("colour", this.getColour());
    	}
        return dataOut;
    }

    @Override
    public String getInfo() {
        return null;
    }

}
