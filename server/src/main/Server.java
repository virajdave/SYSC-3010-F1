package main;
import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import types.Message;
import util.*;

public class Server extends Thread {
    private static final int PACKET_SIZE = 1500;

    private Queue<Message> recvQueue;
    private DatagramSocket socket;
    private Integer port;
    private InetAddress bcast = null;
    
    public Server() {
    	this(null);
    }
    
    public Server(Integer _port) {
    	recvQueue = new LinkedList<>();
    	socket = null;
    	port = _port;
    }
	
    /**
     * Start up the server.
     */
	public void run() {
        try {
        	// Create socket.
            if (port != null) {
            	socket = new DatagramSocket(port);
                //socket.setReuseAddress(true);
                //InetSocketAddress t = new InetSocketAddress(UDP_SERVER_PORT);
                //socket.bind(t);
            } else {
            	socket = new DatagramSocket();
                port = socket.getLocalPort();
            }

            // Receive.
            while (!Thread.interrupted()){
            	// Stick a received message in a packet.
            	byte[] data = new byte[PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                // Create a message from the packet and add it to the queue, notify any waiting receives.
                Message message = new Message(packet);
                synchronized (recvQueue) {
                    recvQueue.add(message);
                	recvQueue.notifyAll();
                }
            }
            
        } catch (SocketException e) {
        	if (e.getMessage().toLowerCase().equals("socket closed") && Thread.interrupted()) {
        		return;
        	}
            e.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
		} finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
	}
	
	/**
	 * Close down the server.
	 */
	@Override
	public void interrupt() {
		super.interrupt();
		socket.close();
	}
	
	/**
	 * Send a message.
	 * @param message
	 */
	public void sendMessage(Message message) {
        try {
            if (socket != null){
            	// Convert message data to bytes, create the packet and send it.
                byte[] sendData = message.getMessage().getBytes("UTF-8");
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, message.getSocketAddress());
                socket.send(sendPacket);
            } else {
                System.err.println("Server not running, cannot send message.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Send a message over broadcast.
	 * @param message
	 */
	public void sendBroadcast(Message message) {
		if (bcast == null) {
			// Get the bcast address if it hasn't been set yet.
			bcast = Net.getBroadcast();
		}
		
        try {
            if (socket != null){
            	// Convert message data to bytes, create the packet and send it to bcast at the given port.
                byte[] sendData = message.getMessage().getBytes("UTF-8");
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, bcast, message.getPort());
                socket.send(sendPacket);
            } else {
                System.err.println("Server not running, cannot send message.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Check for a received message, waiting if the queue is empty.
	 * @return Message
	 */
	public Message recvWait() {
		// Wait if the queue is empty.
        synchronized (recvQueue) {
			if (recvQueue.isEmpty()) {
	            try {
					recvQueue.wait();
					return recvQueue.poll();
				} catch (InterruptedException e) {}
			}
			return recvQueue.poll();
        }
	}
	
	/**
	 * Check for a received message.
	 * @return Message or null if the queue is empty
	 */
	public Message recvMessage() {
		// Grab the first message and return it.
        synchronized (recvQueue) {
        	return recvQueue.poll();
        }
	}
	
	public Integer getPort() {
		return port;
	}
	
	
	/**
	 * TEST
	 */
	public static void main(String[] args) throws UnknownHostException {
        // Check the arguments
        if (args.length != 1) {
            System.out.println("usage: java UDPSender host port");
            return;
        }
        int port = Integer.parseInt(args[0]);
    	InetAddress ip = InetAddress.getByName("localhost");
    	
    	Server s = new Server(port);
    	s.start();
    	
    	Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
            	while (true) {
	            	Message msg = s.recvWait();
	            	if (msg != null) {
	            		System.out.println(msg.toString());
	            	}
            	}
            }
        });
    	t.start();

        Scanner in = new Scanner(System.in);
        String msg = null;
    	System.out.println("Enter text to be sent, ENTER to quit ");
        while (true) {
        	msg = in.nextLine();
            if (msg.length() == 0) break;
            
            
            if (msg.charAt(0) != 'b') {
                s.sendMessage(new Message(msg, ip, port));
            } else {
            	msg = msg.substring(1);
                s.sendBroadcast(new Message(msg, ip, port));
            }
        }
        System.out.println("Closing down");
        in.close();
        s.interrupt();
        t.interrupt();
        try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        System.exit(0);
    }
}
