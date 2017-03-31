package application;

import java.net.InetSocketAddress;
import java.util.Arrays;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import main.Server;
import types.Message;
import util.*;

public class Controller {
	private static final InetSocketAddress addr = new InetSocketAddress("localhost", 3010);
	
    @FXML
    ListView<String> devices;
    @FXML
    Label devicesLabel;
	private Server server;
	
	public Controller() {
		server = new Server();
		server.start();
	}
	
	@FXML
	void initialize() {
		assert devices != null : "fx:id=\"devices\" was not injected.";
		assert devicesLabel != null : "fx:id=\"devicesLabel\" was not injected.";

		server.sendMessage(new Message(Parse.toString("", Codes.W_APP, Codes.T_NETINF), addr));
		Message m = server.recvWait();
		System.out.println(m.getMessage());
		setDevices(m.getMessage());
//		setDevices("00/3:12:0/25:2:0/0:0:1");
	}

    public void stop() throws InterruptedException {
    	server.interrupt();
    	server.join();
    }
	
	public void setDevices(String message) {
		String[] list = message.substring(3).split("/");
		
		devices.setCellFactory(new Callback<ListView<String>, 
            ListCell<String>>() {
                @Override 
                public ListCell<String> call(ListView<String> list) {
                    return new DeviceCell();
                }
            }
        );
		
		if (list.length == 1 && list[0].length() == 0) {
		    devices.setItems(null);
		    devicesLabel.setText("No devices connected");
			devices.setVisible(false);
		} else {
		    ObservableList<String> data = FXCollections.observableArrayList(Arrays.asList(list));
		    devices.setItems(data);
			devices.setVisible(true);
		}
	}
    
    static class DeviceCell extends ListCell<String> {
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
            	String[] parts = item.split(":");
            	String text = "";
            	try {
            		text += "#" + parts[0];
            		int type = Parse.toInt(parts[1]);
            		if (type < TYPENAMES.length && type >= 0) {
                		text += " " + TYPENAMES[type];
            		} else {
            			text += " Unknown Type";
            		}
                	if (Parse.toBool(parts[2])) {
                        setStyle("-fx-text-fill: grey");
                        text += " (Disconnected)";
                	}
            	} catch (Exception e) {
            		text += " - Error Parsing Info";
                    setStyle("-fx-text-fill: red");
            	}
            	setText(text);
            }
        }
    }
    
    static final String[] TYPENAMES = new String[] {
    		"Lights",
    		"Switch",
    		"Mirror",
    		"Thermostat",
    		"Bed"
    };
}
