import java.net.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import types.Message;
import util.*;

public class Server extends Thread {
    private static final int PACKET_SIZE = 1500;

    private Queue<Message> recvQueue;
	private boolean keepRunning;
    private DatagramSocket socket;
    private Integer port;
    private InetAddress bcast = null;
    
    public Server() {
    	this(null);
    }
    
    public Server(Integer _port) {
    	recvQueue = new LinkedList<>();
    	keepRunning = true;
    	socket = null;
    	port = _port;
    }
	
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
            while (keepRunning){
            	byte[] data = new byte[PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                Message message = new Message(packet);
                synchronized (this) {
                    recvQueue.add(message);
                    this.notifyAll();
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
	}
	
	public void close() {
		keepRunning = false;
	}
	
	public void sendMessage(Message message) {
        try {
            if (socket != null){
                byte[] sendData = message.getMessage().getBytes("UTF-8");
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, message.getSocketAddress());
                socket.send(sendPacket);
            } else {
                System.err.println("Server not running, cannot send message.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void sendBroadcast(Message message) {
		if (bcast == null) {
			bcast = Net.getBroadcast();
		}
		
        try {
            if (socket != null){
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
	
	public Message recvMessage() {
		return recvQueue.poll();
	}
	
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
	            	try {
	            		synchronized (s) {
							s.wait();
	            		}
		            	Message msg = s.recvMessage();
		            	if (msg != null) {
		            		System.out.println(msg.toString());
		            	}
					} catch (InterruptedException e) {
						break;
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
        s.close();
        t.interrupt();
        try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        System.exit(0);
    }
}
