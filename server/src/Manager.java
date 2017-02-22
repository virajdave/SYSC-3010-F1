import types.*;

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
			try {
				synchronized (server) {
					server.wait();
    			}
    		} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
    		// Check for new incoming messages.
        	Message msg = server.recvMessage();
        	if (msg != null) {
        		Client c = web.getClient(msg.getAddress());
        		if (c != null) {
        			Device d = c.getDevice(msg.getPort());
        			if (d != null) {
        				// Yay it exists!
        				System.out.println("This device exists.");
        			} else {
        				// Add new device.
            			c.addDevice(msg.getPort());
        				System.out.println("Added new device.");
        			}
        		} else {
        			// Add new client + device.
        			web.addClient(msg.getAddress()).addDevice(msg.getPort());
    				System.out.println("Added new client + device.");
        		}
        	}
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
