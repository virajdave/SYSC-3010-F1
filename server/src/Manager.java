import types.*;
import util.Codes;

public class Manager extends Thread {
	Web web;
	Server server;
	
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
		Integer id = web.get(msg.getSocketAddress());
		if (id == null) {
			if (code == Codes.T_ACK) {
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
				// Create device blah blah
				System.out.println("Device of type: " + type);
				
				// Add id mapping
				id = web.add(msg.getSocketAddress());
				System.out.println("Added device #" + id);
				// Send ack back letting the device know it was connected.
				server.sendMessage(new Message("" + Codes.W_SERVER + Codes.T_ACK, msg.getSocketAddress()));
			} else {
				// Don't know what this is, looking for info with a beat.
				server.sendMessage(new Message("" + Codes.W_SERVER + Codes.T_BEAT, msg.getSocketAddress()));
			}
			return;
		}
		System.out.println("Message from device #" + id + ": "  + msg.getMessage());
		
		// Quick check device ID matches.
		
		switch (code) {
			case Codes.T_BEAT:
				// uh don't know what to do here.
				break;
			case Codes.T_ACK:
				// Check if anything is waiting on an ack?
				break;
			case Codes.T_INPUT:
				// Look through ruleset to see if anything expects this input.
				// Send to device driver.
				break;
			case Codes.T_OUTPUT: 
				// Probably asking for an output? Not sure if this happens.
				break;
			case Codes.T_CONF: 
				// This also probably shouldn't happen?
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
			case Codes.T_INPUT:
				// Look through ruleset to see if anything expects this input.
				// Send to device driver.
				break;
			case Codes.T_OUTPUT: 
				// Probably asking for an output? Not sure if this happens.
				break;
			case Codes.T_CONF: 
				// This also probably shouldn't happen?
				break;
			default:
				System.out.println("wat is dis -> " + msg.toString());
		}
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
