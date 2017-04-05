package main;

import java.util.Observable;
import java.util.Observer;

import devices.Device;
import devices.Null;
import types.*;
import util.Codes;
import util.Log;
import util.Parse;

public class Manager extends Thread implements Observer {
	private static final double BEATRATE = 0.15; // in minutes
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
	 * @param beatrate Rate to send heartbeat    (in miliseconds)
	 * @param timeout  Time for message timeouts (in seconds)
	 */
	public Manager(Server s, double beatrate, int timeout) {
		web = new Web();
		server = s;
		this.timeout = timeout;
		heart = new HeartBeat(server, web, beatrate);
	}

	public void run() {
		server.start();
		heart.start();
		
		Log.out("Server and heartbeat started, waiting to recv messages.");

		while (!Thread.interrupted()) {
			// Wait for incoming messages and deal with them.
			gotMessage(server.recvWait(0));
		}
	}
	
	public void gotMessage(Message msg) {
		if (msg != null) {
			try {
				Parse.toInt(msg.getMessage().substring(0, 2));
			} catch (Exception e) {
				Log.warn("Received invalid OPCode in message: " + msg.toString());
				return;
			}
			
			char who = msg.getMessage().charAt(0);
			switch (who) {
			case Codes.W_DEVICE:
				device(msg);
				break;
			case Codes.W_APP:
				app(msg);
				break;
			default:
				Log.warn("unknown W code -> " + msg.toString());
			}
		}
	}
	
	private void newDevice(Message msg, String[] data, char code) {
		if (code == Codes.T_BEAT) {
			Log.out("Going to add device with inet " + msg.getSocketAddress().toString());
			// Get the device type from the message info.
			try {
				int type = Integer.parseInt(data[2]);

				// Create device and watch for outputs.
				Device d = web.add(msg.getSocketAddress(), type);
				if (!d.getClass().equals(Null.class)) {
					d.addObserver(this);
					Log.out("Added device #" + d.getID() + " of type " + type);
	
					// Send ack back letting the device know it was connected.
					server.sendMessage(new Message(Parse.toString("/", Codes.W_SERVER + "" + Codes.T_ACK, d.getID()), msg.getSocketAddress()));
				}
			} catch (NumberFormatException e) {
				Log.warn("Device type '" + data[2] + "' malformed, from " + msg.toString());
				return;
			} catch (Exception e) {
				Log.warn("Couldn't parse info from " + msg.toString());
				return;
			}
		} else {
			// Unknown device, looking for BEAT device info.
			server.sendMessage(new Message(Parse.toString("", Codes.W_SERVER, Codes.T_BEAT), msg.getSocketAddress()));
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
			newDevice(msg, data, code);
			return;
		}
		Log.out("Message from device #" + d.getID() + ": " + msg.getMessage());

		// Quick check device ID matches what we expect.
		try {
			int id = Integer.parseInt(data[1]);
			
			if (!d.hasID(id)) {
				Log.warn("Device #" + d.getID() + " gave ID " + id + ", suggesting the network has changed and this device should be reset.");
				return;
				// TODO: This would be a problem.
			}
		} catch (NumberFormatException e) {
			Log.warn("Device id '" + data[1] + "' is not an int, from " + msg.toString());
			return;
		} catch (Exception e) {
			Log.warn("Couldn't parse device id, from " + msg.toString());
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
				try {
					d.giveMessage(msg.getMessage().substring(data[0].length() + data[1].length() + 2));
				} catch (Exception e) {
					Log.err("Exception in 'giveMessage' for device driver " + d.getClass(), e);
				}
				server.sendMessage(new Message(Parse.toString("", Codes.W_SERVER, Codes.T_ACK), msg.getSocketAddress()));
				break;
			default:
				Log.warn("unknown device T code -> " + msg.getMessage().toString() + " from device #" + d.getID() +  " with  " + msg.toString());
		}
	}

	/**
	 * Deal with a app message.
	 * @param msg
	 */
	private void app(Message msg) {
		String[] data = msg.getMessage().split("/");
		char code = data[0].charAt(1);

		Log.out("Message from app: " + msg.getMessage());

		int id;
		switch (code) {
			case Codes.T_NETINF:
				// Give back the device network information.
				server.sendMessage(new Message(Parse.toString("/", Codes.W_SERVER + "" + Codes.T_NETINF, web.toString()), msg.getSocketAddress()));
				break;
			case Codes.T_ACK:
				// TODO: implement ACK checking.
				break;
			case Codes.T_DEVINF:
				// Give back requested device info by ID.
				try {
					id = Parse.toInt(data[1]);
					String info = "";
					
					Device d = web.getByID(id);
					if (d != null) {
						try {
							info = d.getInfo();
						} catch (Exception e) {
							Log.err("Exception in 'getInfo' for device driver " + web.getByID(id).getClass(), e);
						}
					} else {
						Log.warn("App giving device ID which does not exist, from " + msg.getMessage());
					}
					// Yes an empty string could be sent back, this is represented as the error code back to the app.
					server.sendMessage(new Message(Parse.toString("/", Codes.W_SERVER + "" + Codes.T_DEVINF, info), msg.getSocketAddress()));
				} catch (NumberFormatException e) {
					Log.warn("App giving malformed device ID, from " + msg.getMessage());
				} catch (NullPointerException e) {
					Log.warn("App giving device ID which does not exist, from " + msg.getMessage());
				} catch (ArrayIndexOutOfBoundsException e) {
					Log.warn("App message missing device ID, from " + msg.getMessage());
				}
				break;
			case Codes.T_DATA:
				/* Parse data:
				 * 0 - device being modified
				 * 1 - what is changing
				 * 2 - new value
				 */
				try {
					id = Parse.toInt(data[1]);
					Data in = new Data(data[2], data[3]);
					
					Device d = web.getByID(id);
					boolean worked = false;
					if (d != null) {
						try {
							worked = d.giveInput(in);
						} catch (Exception e) {
							Log.err("Exception in 'giveInput' for device driver " + web.getByID(id).getClass(), e);
						}
					} else {
						Log.warn("App giving device ID which does not exist, from " + msg.getMessage());
					}
					// Send back acknowledge saying if the request worked.
					server.sendMessage(new Message(Parse.toString("/", Codes.W_SERVER + "" + Codes.T_ACK, worked), msg.getSocketAddress()));
				}  catch (NumberFormatException e) {
					Log.warn("App giving malformed device ID, from " + msg.getMessage());
				} catch (ArrayIndexOutOfBoundsException e) {
					Log.warn("App message missing at least 1 of 3 required pieces of data, from " + msg.getMessage());
				}
				break;
			case Codes.T_DELETE:
				try {
					id = Parse.toInt(data[1]);
					
					Device d = web.getByID(id);
					if (d != null) {
						web.remove(d);
					} else {
						Log.warn("App giving device ID which does not exist, from " + msg.getMessage());
					}
					// Send back acknowledge.
					server.sendMessage(new Message(Parse.toString("", Codes.W_SERVER, Codes.T_ACK), msg.getSocketAddress()));
				}  catch (NumberFormatException e) {
					Log.warn("App giving malformed device ID, from " + msg.getMessage());
				} catch (ArrayIndexOutOfBoundsException e) {
					Log.warn("App message missing device ID, from " + msg.getMessage());
				}
				break;
			default:
				Log.warn("unknown app T code -> " + msg.toString());
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
			Log.err("UH OH");
			return;
		}

		// Send out the message to the correct device.
		Device d = (Device) arg0;
		String msg = (String) arg1;
		Message m = new Message(Parse.toString("/", Codes.W_SERVER + "" + Codes.T_DATA, msg), web.get(d));
		server.sendMessage(m);
		Log.out("Message to device #" + d.getID() + ": " + m.getMessage());
	}

	public static void main(String[] args) {
		new Manager(new Server(3010)).start();
	}
}
