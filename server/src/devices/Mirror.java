package devices;

import java.net.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import types.Data;
import util.Parse;

public class Mirror extends Device {
    private int thermo;
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
        thermo = -1;
    }
    
    public Mirror(HashMap<String, String> data) {
		// Use the DB property if it has been set.
    	currentColour 	= data.containsKey("colour") 	? data.get("colour") 				: "#2E99A9";
    	currStop 		= data.containsKey("stop") 		? data.get("stop") 					: "3031";
    	currRoute 		= data.containsKey("route") 	? data.get("route") 				: "104"; 	
    	currDirection 	= data.containsKey("direction") ? data.get("direction") 			: "0";
    	lon 			= data.containsKey("lon") 		? data.get("lon") 					: "-75.6981200";
    	lat 			= data.containsKey("lat") 		? data.get("lat") 					: "45.4111700";
    	thermo 			= data.containsKey("thermo") 	? Parse.toInt(data.get("thermo")) 	: -1;
    	try {
			send(getWeather());
			send(getBus());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	send(getTime());
    	send(thermoTemp());
	}

    /**
     * Creates the JSON string with the time
     * @return JSON string of time
     */
    public String getTime() {
        long time = System.currentTimeMillis();
        return "t"+ time;
    }

    /**
     * Changes the location of the weather data
     * @param loc comma seperated lat and lon, eg 58,96
     */
    public void setLoc(String loc) {
    	String[] temp;
        String delimeter = ",";
        temp = loc.split(delimeter);
        lon = temp[0];
        lat = temp[1];
        setProperty("lon", this.lon);
        setProperty("lat", this.lat);
        try {
			send(this.getWeather());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * Changes the bus info to collect from OCTranspo
     * @param routeInfo comma separated station,route,direction
     */
    public boolean changeRoute(String routeInfo) {
    	String[] temp;
        temp = routeInfo.split(",");
        
        // Check all values are integers.
        for (String t : temp) {
        	try {
        		Parse.toInt(t);
        	} catch (NumberFormatException e) {
        		return false;
        	}
        }
        
        // Set bus info.
    	setStation(temp[0]);
    	setRoute(temp[1]);
    	setDirection(temp[2]);
    	try {
			String dataOut = getBus();
			send("d" + this.currDirection);
			send(dataOut);	
	    	return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return false;
    }
    
    /**
     * Changes the set station
     * @param station Station number
     */
    private void setStation (String station) {
    	this.currStop = station;
    	setProperty("stop", this.currStop);
    }
    
    /**
     * Changes the route to look for at the station
     * @param route route number
     */
    private void setRoute (String route) {
    	this.currRoute = route;
    	setProperty("route", this.currRoute);
    }
    
    /**
     * Changes the direction of a route to look at
     * @param direction 0 for both, 1 for east and 2 for west
     */
    private void setDirection (String direction) {
    	this.currDirection = direction;
    	setProperty("direction", this.currDirection);
    }
    
    
    /**
     * Gives the current station
     * @return station number as a string
     */
    public String getStation () {
    	return this.currStop;
    }
    
    /**
     * Gives the current route
     * @return current route as a string
     */
    public String getRoute () {
    	return this.currRoute;
    }
    
    /**
     * Gives the direction interested in
     * @return direction number as string
     */
    public String getDirection () {
    	return this.currDirection;
    }
    
    
    /**
     * Collects bus information from octranspo api
     * @return string to be sent to mirror device
     * @throws IOException no internet access
     */
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
    
    /**
     * Builds the url for the api
     * @param stop stop number
     * @param route route number
     * @return url to access api
     */
    private String buildBusURL(String stop, String route) {
    	String key = "appID=7f6091d8&apiKey=4be816c142bfb4100421b9cbdef4fb9a";
    	String strStop = "&stopNo=" + stop;
    	String strRoute = "&routeNo=" + route;
    	String url = "http://api.octranspo1.com/v1.2/GetNextTripsForStop?";
    	String full_api_url = url + key + strRoute + strStop + "&format=json";
        return full_api_url;
    }
    
    
    /**
     * Collects the weather from openWeather api
     * @return the string to be sent to the mirror
     * @throws IOException no internet access
     */
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
    
    /**
     * Builds the url to access the api
     * @return url for openweather api
     */
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

    /**
     * Changes the currently set colour of the mirror
     * @param colour string hex code eg #FFFFFF
     */
    private void setColour(String colour) {
    	// Check the new colour matches the required pattern.
        String HEX_PATTERN = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        Pattern r = Pattern.compile(HEX_PATTERN);
        Matcher m = r.matcher(colour);
        if (m.find( )) {
        	// Set the new colour.
            this.currentColour = m.group(0);
            setProperty("colour", this.currentColour);
        }
    }
    
    /**
     * Returns the current colour of the mirror
     * @return colour in format #colour
     */
    private String getColour() {
    	return this.currentColour;
    }
    
    /**
     * Gets the termo temp and sends to mirror
     * @return string to send to mirror
     */
    public String thermoTemp() {
    	String sendString = "h";
    	try{
    		if (thermo != -1) {
    			Thermostat thermostat = (Thermostat) getDevice(thermo); 
    			Data temp = thermostat.requestOutput(new Data("temp", ""));
    			if (temp != null) {
    				sendString += temp.get();
    			}
    		}
    		
    	} catch (Exception e) {
    		System.out.println("No thermo Device");
    	}
        return sendString;
    }
    
    /**
     * Sets the thermostat the mirror is concerned with
     */
    public void setThermoDevice(int t) {
    	this.thermo = t;
    	setProperty("thermo", Parse.toString(this.thermo));
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
        } else if (msg.equals("thermo")) {
        	dataOut = thermoTemp();
        } else {
        	dataOut = "N";
        }
        send(dataOut);
    }

    @Override
    public boolean giveInput(Data in) {
    	if (in.is("colour")) {
    		// Set the new colour and then send it out.
    		this.setColour(in.get());
    		send("c" + currentColour);
    	} else if (in.is("route")) {
    		return this.changeRoute(in.get());
    	} else if (in.is("loc")) {
    		this.setLoc(in.get());
    	} else if (in.is("thermo")) {
    		setThermoDevice(Parse.toInt(in.get()));
    	} else {
    		return false;
    	}
    	return true;
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
