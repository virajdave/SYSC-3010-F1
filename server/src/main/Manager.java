package main;

import java.net.InetSocketAddress;
import java.util.Observable;
import java.util.Observer;

import devices.Device;
import types.*;
import util.Codes;
import util.Parse;

public class Manager extends Thread implements Observer {
	private Web web;
	private Server server;
	private int beatrate;
	private int timeout;

	public Manager(Server s) {
		web = new Web();
		server = s;
		beatrate = 10;
		timeout = 30;
	}

	public Manager(Server s, int beatrate, int timeout) {
		web = new Web();
		server = s;
		this.beatrate = beatrate;
		this.timeout = timeout;
	}

	public void run() {
		server.start();
		System.out.println("going");

		while (true) {
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

	private void device(Message msg) {
		char code = msg.getMessage().charAt(1);
		String[] data = msg.getMessage().substring(2).split("/");

		// Get the device, if it doesn't exist try adding it.
		Device d = web.get(msg.getSocketAddress());
		if (d == null) {
			if (code == Codes.T_BEAT) {
				System.out.println("Going to add device.");
				// Get the type from the message info.
				String info = data[0];
				int type = -1;
				try {
					type = Integer.parseInt(info);
				} catch (NumberFormatException e) {
					System.out.println("Device type '" + info + "' unknown.");
					return;
				} catch (Exception e) {
					System.out.println("Couldn't parse info.");
					return;
				}

				// Create device and watch for events.
				d = web.add(msg.getSocketAddress(), type);
				d.addObserver(this);
				System.out.println("Added device #" + d.getID() + " of type " + type);

				// Send ack back letting the device know it was connected.
				server.sendMessage(new Message(Parse.toString("", Codes.W_SERVER, Codes.T_ACK, d.getID()),
						msg.getSocketAddress()));
			} else {
				System.out.println("Unknown device, sending back beat.");
				// Don't know what this is, looking for info with a beat.
				server.sendMessage(
						new Message(Parse.toString("", Codes.W_SERVER, Codes.T_BEAT), msg.getSocketAddress()));
			}
			return;
		}
		System.out.println("Message from device #" + d.getID() + ": " + msg.getMessage());

		// Quick check device ID matches.

		// Do different things depending on what the code is.
		switch (code) {
		case Codes.T_BEAT:
			// Could be ignored.
			break;
		case Codes.T_ACK:
			// TODO: implement ACK checking.
			break;
		case Codes.T_DATA:
			// Send to device driver.
			d.giveMessage(msg.getMessage().substring(2));
			server.sendMessage(new Message(Parse.toString("", Codes.W_SERVER, Codes.T_ACK), msg.getSocketAddress()));
			break;
		default:
			System.out.println("unknown device T -> " + msg.toString());
		}
	}

	private void app(Message msg) {
		char code = msg.getMessage().charAt(1);
		String[] data = msg.getMessage().substring(2).split("/");

		int id;
		switch (code) {
		case Codes.T_NETINF:
			server.sendMessage(new Message(Parse.toString("", Codes.W_SERVER, Codes.T_NETINF, web.toString()),
					msg.getSocketAddress()));
			break;
		case Codes.T_ACK:
			// Check if anything is waiting on an ack?
			break;
		case Codes.T_DEVINF:
			id = Parse.toInt(data[0]);
			server.sendMessage(
					new Message(Parse.toString("", Codes.W_SERVER, Codes.T_DEVINF, web.getByID(id).getInfo()),
							msg.getSocketAddress()));
			break;
		case Codes.T_DATA:
			id = Parse.toInt(data[0]);
			Data in = new Data(data[1], data[2]);
			web.getByID(id).giveInput(in);
			server.sendMessage(new Message(Parse.toString("", Codes.W_SERVER, Codes.T_ACK), msg.getSocketAddress()));
			break;
		default:
			System.out.println("unknown app T -> " + code);
		}
	}

	public void doHeartBeat() {
		for (InetSocketAddress addr : web.addrList()) {
			server.sendMessage(new Message(Parse.toString("", Codes.W_SERVER, Codes.T_BEAT), addr));
		}
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// Check correct types.
		if (!(arg0 instanceof Device) || !(arg1 instanceof String)) {
			System.err.println("UH OH");
			return;
		}

		Device d = (Device) arg0;
		String msg = (String) arg1;
		server.sendMessage(new Message(Codes.W_SERVER + Codes.T_DATA + msg, web.get(d)));
	}

	public static void main(String[] args) {
		// Check the arguments
		if (args.length != 1) {
			System.out.println("usage: java UDPSender host port");
			return;
		}
		int port = Integer.parseInt(args[0]);

		new Manager(new Server(port)).start();
	}
}
