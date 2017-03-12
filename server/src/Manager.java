import java.util.Observable;
import java.util.Observer;

import devices.Device;
import types.*;
import util.Codes;

public class Manager extends Thread implements Observer {
	private Web web;
	private Server server;
	
	public Manager(int port) {
		web = new Web();
		server = new Server(port);
	}

	public void run() {
    	server.start();
		System.out.println("going");
    	
    	while (true) {
    		// Check for new incoming messages.
        	Message msg = server.recvWait();
        	if (msg != null) {
        		char who = msg.getMessage().charAt(0);
        		switch (who) {
        			case Codes.W_DEVICE:
        				device(msg); break;
        			case Codes.W_APP:
        				app(msg); break;
        			default:
        				System.out.println("wat is dis -> " + msg.toString());
        		}
        	}
    	}
	}
	
	private void device(Message msg) {
		char code = msg.getMessage().charAt(1);
		
		// Get the device, if it doesn't exist try adding it.
		Device d = web.get(msg.getSocketAddress());
		if (d == null) {
			if (code == Codes.T_ACK) {
				// Get the type from the message info.
				String[] info = msg.getMessage().substring(2).split("/");
				int type = -1;
				try {
					type = Integer.parseInt(info[1]);
				} catch (NumberFormatException e) {
					System.out.println("Device type '" + info[1] + "' unknown.");
					return;
				} catch (Exception e) {
					System.out.println("Couldn't parse info.");
					return;
				}
				
				// Create device and watch for events.
				d = web.add(msg.getSocketAddress(), type);
				d.addObserver(this);
				System.out.println("Added device #" + d.getID() + " of type " + type);
				
				// Send ack back letting the device know it was connected.
				server.sendMessage(new Message("" + Codes.W_SERVER + Codes.T_ACK, msg.getSocketAddress()));
			} else {
				// Don't know what this is, looking for info with a beat.
				server.sendMessage(new Message("" + Codes.W_SERVER + Codes.T_BEAT, msg.getSocketAddress()));
			}
			return;
		}
		System.out.println("Message from device #" + d.getID() + ": "  + msg.getMessage());
		
		// Quick check device ID matches.
		
		// Do different things depending on what the code is.
		switch (code) {
			case Codes.T_BEAT:
				// uh don't know what to do here.
				break;
			case Codes.T_ACK:
				// Check if anything is waiting on an ack?
				break;
			case Codes.T_DATA:
				// Send to device driver.
				break;
			default:
				System.out.println("wat is dis -> " + msg.toString());
		}
	}
	
	private void app(Message msg) {
		char code = msg.getMessage().charAt(1);
		
		switch (code) {
			case Codes.T_BEAT:
				// Send back an ack.
				server.sendMessage(new Message("" + Codes.W_SERVER + Codes.T_ACK, msg.getSocketAddress()));
				break;
			case Codes.T_ACK:
				// Check if anything is waiting on an ack?
				break;
			case Codes.T_DATA:
				// Figure out what this is and what to do with it.
			default:
				System.out.println("wat is dis -> " + msg.toString());
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// Check correct types.
		if (!(arg0 instanceof Device) || !(arg1 instanceof String)) {
			System.err.println("UH OH");
			return;
		}
		
		Device d = (Device)arg0;
		String msg = (String)arg1;
		server.sendMessage(new Message(Codes.W_SERVER + Codes.T_DATA + msg, web.get(d)));
	}
	
	public static void main(String[] args) {
        // Check the arguments
        if (args.length != 1) {
            System.out.println("usage: java UDPSender host port");
            return;
        }
        int port = Integer.parseInt(args[0]);
        
		new Manager(port).start();
	}
}
