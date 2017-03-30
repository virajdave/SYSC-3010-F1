package main;

import java.net.InetSocketAddress;
import java.util.HashSet;

import devices.Device;
import types.Message;
import types.Web;
import util.Codes;
import util.Log;
import util.Parse;

public class HeartBeat extends Thread {
	private Server server;
	private Web web;
	private long rate;
	private HashSet<InetSocketAddress> addrSet;
	private int sync;

	public HeartBeat(Server s, Web w, double r) {
		server = s;
		web = w;
		rate = Math.round(r * 1000);
		addrSet = null;
	}

	public void run() {
		// If the rate is not positive then close down the heart.
		if (rate <= 0) {
			return;
		}
		
		// Beat until the heart is interrupted.
		while (!Thread.interrupted()) {
			try {
				beat();
				Thread.sleep(rate * 51);
			} catch (InterruptedException e) {
				// EAT.
			}
		}
	}

	/**
	 * Received a message from a specific device.
	 * @param d
	 */
	public void recved(Device d) {
		// Remove from set of devices we are waiting on.
		synchronized (this) {
			if (addrSet != null) {
				addrSet.remove(web.get(d));
			}
		}
		// Mark the device as reconnected.
		if (d.isDead()) {
			d.setDead(false);
			Log.out("Device #" + d.getID() + " has reconnected.");
		}
	}


	/**
	 * Reset the addr set and send out a heartbeat.
	 * @throws InterruptedException 
	 */
	public void beat() {
		try {
			synchronized (this) {
				addrSet = new HashSet<>(web.addrList());
			}
			send();
			synchronized (this) {
				for (InetSocketAddress addr : addrSet) {
					if (web.get(addr).isDead()) {
						addrSet.remove(addr);
					}
				}
			}
			Thread.sleep(rate * 3);
			send();					
			Thread.sleep(rate * 3);
			send();
			Thread.sleep(rate * 3);
			clean();
		} catch (InterruptedException e) {
			// EAT.
		}
	}

	/**
	 * Send a heartbeat to all devices in the set.
	 */
	private void send() {
		synchronized (this) {
			if (addrSet != null) {
				for (InetSocketAddress addr : addrSet) {
					server.sendMessage(new Message(Parse.toString("", Codes.W_SERVER, Codes.T_BEAT), addr));
				}
			}
		}
	}

	/**
	 * Send a heartbeat to all devices in the set.
	 */
	private void clean() {
		synchronized (this) {
			if (addrSet != null) {
				// Mark any remaining devices as disconnected.
				for (InetSocketAddress addr : addrSet) {
					Device d = web.get(addr);
					if (!d.isDead()) {
						d.setDead(true);
						Log.out("Device #" + d.getID() + " has disconnected.");
					}
				}
				addrSet = null;
			}
		}
	}
}
