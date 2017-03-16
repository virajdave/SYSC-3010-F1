package main;

import java.net.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;

import types.Message;

public class ServerStub extends Server {
	private Queue<Message> recvQueue;
	private HashMap<InetSocketAddress, Queue<String>> sendQueue;
	private boolean started;
	private Integer port;
	private int currPort;

	public ServerStub() {
		this(null);
	}

	public ServerStub(Integer _port) {
		port = _port;
		recvQueue = new LinkedList<>();
		sendQueue = new HashMap<>();
		started = false;
		currPort = 0;
	}

	/**
	 * Start up the server.
	 */
	public void run() {
		started = true;
	}

	/**
	 * Close down the server.
	 */
	@Override
	public void interrupt() {
		super.interrupt();
		started = false;
	}

	/**
	 * Send a message.
	 * 
	 * @param message
	 */
	public void sendMessage(Message message) {
		if (started) {
			if (!sendQueue.containsKey(message.getSocketAddress())) {
				sendQueue.put(message.getSocketAddress(), new LinkedList<>());
			}
			sendQueue.get(message.getSocketAddress()).add(new String(message.getMessage().getBytes(), 0, Server.PACKET_SIZE));
		} else {
			System.err.println("Server not running, cannot send message.");
		}
	}

	/**
	 * Send a message over broadcast.
	 * 
	 * @param message
	 */
	public void sendBroadcast(Message message) {
		sendMessage(message);
	}

	/**
	 * Check for a received message, waiting if the queue is empty.
	 * 
	 * @return Message
	 */
	public Message recvWait() {
		// Wait if the queue is empty.
		synchronized (recvQueue) {
			if (recvQueue.isEmpty()) {
				try {
					recvQueue.wait();
					return recvQueue.poll();
				} catch (InterruptedException e) {
				}
			}
			return recvQueue.poll();
		}
	}

	/**
	 * Check for a received message.
	 * 
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

	public void giveMessage(Message message) {
		message = new Message(new String(message.getMessage().getBytes(), 0, Server.PACKET_SIZE), message.getSocketAddress());
		synchronized (recvQueue) {
			recvQueue.add(message);
			recvQueue.notifyAll();
		}
	}

	public InetSocketAddress giveMessageNewAddr(String message) {
		InetSocketAddress addr = new InetSocketAddress("127.0.0.1", currPort++);
		giveMessage(new Message(message, addr));
		return addr;
	}

	public String getMessage(InetSocketAddress addr) {
		if (!sendQueue.containsKey(addr)) {
			return null;
		}
		return sendQueue.get(addr).poll();
	}
}
