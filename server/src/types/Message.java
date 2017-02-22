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
	
	public Message(String message, SocketAddress address) {
		this.message = message;
		this.address = (InetSocketAddress)address;
	}
	
	public Message(DatagramPacket packet) {
		this.message = new String(packet.getData()).trim();
		this.address = (InetSocketAddress)packet.getSocketAddress();
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
