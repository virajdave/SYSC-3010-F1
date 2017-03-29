package main;

import java.util.Observable;
import java.util.Observer;

import devices.Device;
import types.*;
import util.Codes;
import util.Parse;

public class Manager extends Thread implements Observer {
	private static final int BEATRATE = 10; // in minutes
	private static final int TIMEOUT = 30;  // in seconds

	private Web web;
	private Server server;
	private HeartBeat heart;
	private int timeout;

	/**
	 * Create a new Manager with defaults.
	 * @param s Server to use for sending messages
	 */
	public Manager(Server s) {
		this(s, BEATRATE, TIMEOUT);
	}

	/**
	 * Create a new Manager.
	 * @param s        Server to use for sending messages
	 * @param beatrate Rate to send heartbeat (in minutes)
	 * @param timeout  Time for message timeouts (in seconds)
	 */
	public Manager(Server s, int beatrate, int timeout) {
		web = new Web();
		server = s;
		this.timeout = timeout;
		heart = new HeartBeat(server, web, beatrate);
	}

	public void run() {
		server.start();
		heart.start();

		while (!Thread.interrupted()) {
			// Check for new incoming messages.
			Message msg = server.recvWait();
			if (msg != null) {
				char who = msg.getMessage().charAt(0);
				switch (who) {
				case Codes.W_DEVICE:
					device(msg);
					break;
				case Codes.W_APP:
					app(msg);
					break;
				default:
					System.out.println("unknown W -> " + msg.toString());
				}
			}
		}
	}

	/**
	 * Deal with a device message.
	 * @param msg
	 */
	private void device(Message msg) {
		String[] data = msg.getMessage().split("/");
		char code = data[0].charAt(1);

		// Get the device, if it doesn't exist try adding it.
		Device d = web.get(msg.getSocketAddress());
		if (d == null) {
			if (code == Codes.T_BEAT) {
				System.out.println("Going to add device.");
				// Get the device type from the message info.
				try {
					int type = Integer.parseInt(data[2]);

					// Create device and watch for outputs.
					d = web.add(msg.getSocketAddress(), type);
					d.addObserver(this);
					System.out.println("Added device #" + d.getID() + " of type " + type);

					// Send ack back letting the device know it was connected.
					server.sendMessage(new Message(Parse.toString("", Codes.W_SERVER, Codes.T_ACK, d.getID()), msg.getSocketAddress()));
				} catch (NumberFormatException e) {
					System.out.println("Device type '" + data[2] + "' unknown.");
					return;
				} catch (Exception e) {
					System.out.println("Couldn't parse info.");
					return;
				}
			} else {
				// Unknown device, looking for BEAT device info.
				server.sendMessage(
						new Message(Parse.toString("", Codes.W_SERVER, Codes.T_BEAT), msg.getSocketAddress()));
			}
			return;
		}
		System.out.println("Message from device #" + d.getID() + ": " + msg.getMessage());

		// Quick check device ID matches what we expect.
		try {
			int id = Integer.parseInt(data[1]);
			
			if (!d.hasID(id)) {
				// TODO: This would be a problem.
			}
		} catch (NumberFormatException e) {
			System.out.println("Device id '" + data[1] + "' is not an int.");
			return;
		} catch (Exception e) {
			System.out.println("Couldn't parse device id.");
			return;
		}

		// Do different things depending on what the code is.
		switch (code) {
			case Codes.T_ACK:
				// TODO: implement ACK checking.
			case Codes.T_BEAT:
				heart.recved(d);
				break;
			case Codes.T_DATA:
				// Send data to device driver.
				d.giveMessage(msg.getMessage().substring(3));
				server.sendMessage(new Message(Parse.toString("", Codes.W_SERVER, Codes.T_ACK), msg.getSocketAddress()));
				break;
			default:
				System.out.println("unknown device T -> " + msg.toString());
		}
	}

	/**
	 * Deal with a app message.
	 * @param msg
	 */
	private void app(Message msg) {
		String[] data = msg.getMessage().split("/");
		char code = data[0].charAt(1);

		int id;
		switch (code) {
			case Codes.T_NETINF:
				// Give back the device network information.
				server.sendMessage(new Message(Parse.toString("", Codes.W_SERVER, Codes.T_NETINF, web.toString()), msg.getSocketAddress()));
				break;
			case Codes.T_ACK:
				// TODO: implement ACK checking.
				break;
			case Codes.T_DEVINF:
				// Give back requested device info by ID.
				try {
					id = Parse.toInt(data[1]);
					server.sendMessage(new Message(Parse.toString("", Codes.W_SERVER, Codes.T_DEVINF, web.getByID(id).getInfo()), msg.getSocketAddress()));
				} catch (NumberFormatException e) {
					// Device ID is malformed.
				} catch (NullPointerException e) {
					// Device with ID does not exist.
				}
				break;
			case Codes.T_DATA:
				/* Parse data:
				 * 0 - device being modified
				 * 1 - what is changing
				 * 2 - new value
				 */
				id = Parse.toInt(data[1]);
				Data in = new Data(data[2], data[3]);
				web.getByID(id).giveInput(in);
				// Send back acknowledge.
				server.sendMessage(new Message(Parse.toString("", Codes.W_SERVER, Codes.T_ACK), msg.getSocketAddress()));
				break;
			default:
				System.out.println("unknown app T -> " + code);
		}
	}
	
	/**
	 * Trigger a heartbeat manually.
	 */
	public void doHeartBeat() {
		heart.beat();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// Check correct types.
		if (!(arg0 instanceof Device) || !(arg1 instanceof String)) {
			System.err.println("UH OH");
			return;
		}

		// Send out the message to the correct device.
		Device d = (Device) arg0;
		String msg = (String) arg1;
		server.sendMessage(new Message(Codes.W_SERVER + Codes.T_DATA + msg, web.get(d)));
	}

	public static void main(String[] args) {
		new Manager(new Server(3010)).start();
	}
}
