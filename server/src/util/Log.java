package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Log {

	private static final boolean DEBUG = true;
	private static final String FILENAME = "server.log";
    private static Logger logger;
	
    /**
     * Initialize the logger.
     * @param file
     */
	public static void init(String file) {
		
		if (file.length() == 0) {
			file = FILENAME;
		}

	    logger = Logger.getLogger("Log");
	    FileHandler fh;  

	    try {
	        // Setup the formatter
	        fh = new FileHandler(file);
	        logger.addHandler(fh);
	        fh.setFormatter(new Formatter() {
	            @Override
	            public String format(LogRecord record) {
	            	String level = record.getLevel().toString().equals("INFO") ? "INFO" : record.getLevel().toString().equals("WARNING") ? "WARN" : "ERROR";
	            			
	                SimpleDateFormat logTime = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	                return level
	                        + " (" + logTime.format(Calendar.getInstance().getTime()) + ") "
	                        + record.getMessage() + "\n";
	            }
	        });
		    logger.setUseParentHandlers(false);
	    } catch (Exception e) {
	        e.printStackTrace();  
	    }
	}
	
	private static void checkStarted() {
		if (logger == null) {
			init("");
		}
	}
	
	/**
	 * Log a general message.
	 * @param message
	 */
	public static void out(String message) {
		checkStarted();
		if (DEBUG) {
			System.out.println("INFO: " + message);
		}
		logger.info(message);
	}

	
	/**
	 * Log a warning message.
	 * @param message
	 */
	public static void warn(String message) {
		checkStarted();
		if (DEBUG) {
			System.out.println("WARN: " + message);
		}
		logger.warning(message);
	}
	
	/**
	 * Log an error message.
	 * @param message
	 */
	public static void err(String message) {
		checkStarted();
		if (DEBUG) {
			System.err.println("ERROR: " + message);
		}
		logger.severe(message);
	}
	
}
