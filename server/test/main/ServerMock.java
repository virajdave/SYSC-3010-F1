package main;

import java.net.*;
import java.nio.BufferOverflowException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;

import types.Message;

public class ServerMock extends Server {
	private static final int TIMEOUT = 10;

	private Queue<Message> recvQueue;
	private HashMap<InetSocketAddress, Queue<String>> sendQueue;
	private boolean started;
	private Integer port;
	private int currPort;

	public ServerMock() {
		this(null);
	}

	public ServerMock(Integer _port) {
		port = _port;
		recvQueue = new LinkedList<>();
		sendQueue = new HashMap<>();
		started = false;
		currPort = 100;
	}

	/**
	 * Start up the server.
	 */
	@Override
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
	@Override
	public void sendMessage(Message message) {
		if (message.getMessage().length() > PACKET_SIZE) {
			throw new BufferOverflowException();
		}
		if (started) {
			if (!sendQueue.containsKey(message.getSocketAddress())) {
				sendQueue.put(message.getSocketAddress(), new LinkedList<>());
			}
			sendQueue.get(message.getSocketAddress()).add(message.getMessage());
		} else {
			System.err.println("Server not running, cannot send message.");
		}
	}

	/**
	 * Send a message over broadcast.
	 * 
	 * @param message
	 */
	@Override
	public void sendBroadcast(Message message) {
		sendMessage(message);
	}

	/**
	 * Check for a received message, waiting if the queue is empty.
	 * 
	 * @return Message
	 */
	public Message recvWait(int timeout) {
		// Wait if the queue is empty.
		synchronized (recvQueue) {
			if (recvQueue.isEmpty()) {
				try {
					recvQueue.wait(timeout);
					return recvQueue.poll();
				} catch (InterruptedException e) {
					e.printStackTrace();
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
	@Override
	public Message recvMessage() {
		// Grab the first message and return it.
		synchronized (recvQueue) {
			return recvQueue.poll();
		}
	}

	@Override
	public Integer getPort() {
		return port;
	}

	public void giveMessage(Message message) {
		if (message.getMessage().length() > PACKET_SIZE) {
			throw new BufferOverflowException();
		}
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
		int timeout = 0;
		String s;
		while ((s = sendQueue.containsKey(addr) ? sendQueue.get(addr).poll() : null) == null && timeout++ < TIMEOUT) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		return s;
	}
}
