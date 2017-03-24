package types;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class Message {
	private String message;
	private InetSocketAddress address;

	/**
	 * Create a message using address and port.
	 * @param message
	 * @param address
	 * @param port
	 */
	public Message(String message, InetAddress address, int port) {
		this.message = message;
		this.address = new InetSocketAddress(address, port);
	}

	/**
	 * Create a message using socket address.
	 * @param message
	 * @param address
	 * @param port
	 */
	public Message(String message, InetSocketAddress address) {
		this.message = message;
		this.address = (InetSocketAddress) address;
	}

	/**
	 * Create a message converting directly from a packet.
	 * @param message
	 * @param address
	 * @param port
	 */
	public Message(DatagramPacket packet) {
		this.message = new String(packet.getData(), 0, packet.getLength());
		try {
			this.address = (InetSocketAddress) packet.getSocketAddress();
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("packet requires a valid socket address, " + e.getMessage());
		}
	}

	/**
	 * Get the attached message.
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Get the attached socket address.
	 */
	public InetSocketAddress getSocketAddress() {
		return this.address;
	}

	/**
	 * Get the attached address.
	 */
	public InetAddress getAddress() {
		return this.address.getAddress();
	}

	/**
	 * Get the attached port.
	 */
	public int getPort() {
		return this.address.getPort();
	}

	/**
	 * Convert to a string 'addr:port message'
	 */
	public String toString() {
		return this.getAddress() + ":" + this.getPort() + " " + this.getMessage();
	}
}
