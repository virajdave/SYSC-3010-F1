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
    		// Check for new incoming messages.
        	Message msg = server.recvWait();
        	if (msg != null) {
        		Integer d = web.get(msg.getSocketAddress());
        		if (d != null) {
    				System.out.println("This device exists.");
        		} else {
        			web.add(msg.getSocketAddress());
    				System.out.println("Added new device.");
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
