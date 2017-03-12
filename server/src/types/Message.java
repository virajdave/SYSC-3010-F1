package types;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Message {
	private String message;
	private InetSocketAddress address;
	
	public Message(String message, InetAddress address, int port) {
		this.message = message;
		this.address = new InetSocketAddress(address, port);
	}
	
	public Message(String message, InetSocketAddress address) {
		this.message = message;
		this.address = (InetSocketAddress)address;
	}
	
	public Message(DatagramPacket packet) {
		this.message = new String(packet.getData(), 0, packet.getLength());
		try {
			this.address = (InetSocketAddress)packet.getSocketAddress();
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("packet requires a valid socket address, " + e.getMessage());
		}
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public InetSocketAddress getSocketAddress() {
		return this.address;
	}
	
	public InetAddress getAddress() {
		return this.address.getAddress();
	}
	
	public int getPort() {
		return this.address.getPort();
	}
	
	public String toString() {
		return this.getAddress() + ":" + this.getPort() + " " + this.getMessage();
	}
}
