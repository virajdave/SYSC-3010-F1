package main;

import java.net.InetSocketAddress;

import devices.Device;
import types.Message;
import types.Web;
import util.Codes;
import util.Parse;

public class HeartBeat extends Thread {
	private Server server;
	private Web web;
	private long rate;

	public HeartBeat(Server s, Web w, long r) {
		server = s;
		web = w;
		rate = r * 1000;
	}

	public void run() {
		while (!Thread.interrupted()) {
			doHeartBeat();
			try {
				Thread.sleep(rate);
			} catch (InterruptedException e) {
			}
		}
	}

	public void recved(Device d) {
		// TODO: categorize devices that have sent messages so that we know which are working.
	}

	public void doHeartBeat() {
		for (InetSocketAddress addr : web.addrList()) {
			server.sendMessage(new Message(Parse.toString("", Codes.W_SERVER, Codes.T_BEAT), addr));
		}
	}
}
