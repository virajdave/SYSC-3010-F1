import java.net.InetSocketAddress;
import java.util.Observable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.DefaultListModel;

import main.Server;
import types.Message;
import util.Codes;
import util.Log;
import util.Parse;

public class Model extends Observable {
	private static final int SERVER_TIMEOUT = 1000;
	private static final int NET_RATE = 1000;
	private static final InetSocketAddress serverAddr = new InetSocketAddress("localhost", 3010);

	private Server s;
	private DefaultListModel<String> devices;
	private ExecutorService executor;

	public Model(DefaultListModel<String> devices) {
		this.s = new Server();
		s.start();
		this.devices = devices;
		this.executor = Executors.newFixedThreadPool(3);
	}

	public void start() {
		this.setChanged();
		this.notifyObservers("Loading...");
		
		Model m = this;
		queueUp(new Runnable() {
			@Override
			public void run() {
				while (!Thread.interrupted()) {
					// Update the net info.
					try {
						queueUp(new Runnable() {
							@Override
							public void run() {
								updateNetInfo();
							}
						}, SERVER_TIMEOUT);
					} catch (TimeoutException e) {
						m.setChanged();
						m.notifyObservers("Server disconnected.");
					}
					
					try {
						Thread.sleep(NET_RATE);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	private void queueUp(Runnable run) {
		try {
			queueUp(run, 0);
		} catch (TimeoutException e) {
			// EAT.
		}
	}

	private void queueUp(Runnable run, int timeout) throws TimeoutException {
		try {
			if (timeout == 0) {
				executor.submit(run);
			} else {
				executor.submit(run).get(timeout, TimeUnit.MILLISECONDS);
			}
		} catch (InterruptedException | ExecutionException e) {
			// EAT.
			e.printStackTrace();
		}
	}

	public void selectDevice(int index) {
		System.out.println(index);
	}

	private void updateNetInfo() {
		s.sendMessage(new Message(Parse.toString("", Codes.W_APP, Codes.T_NETINF), serverAddr));
		String msg = s.recvWait().getMessage();
		devices.removeAllElements();
		if (msg.substring(0, 2).equals(Parse.toString("", Codes.W_SERVER, Codes.T_NETINF))) {
			for (String dev : msg.substring(3).split("/")) {
				String info[] = dev.split(":");
				String str = "#" + info[0];
				str += " " + info[1]; // TODO: Show actual type.
				str += Parse.toBool(info[2]) ? " (Disconnected)" : "";
				devices.addElement(str);
			}
		}
		this.setChanged();
		this.notifyObservers("");
	}

	private void getDeviceInfo(int id) {
		s.sendMessage(new Message(Parse.toString("/", Codes.W_APP + "" + Codes.T_DEVINF, id), serverAddr));
		Message msg = s.recvWait();
		this.setChanged();
		this.notifyObservers(msg.toString());
	}
}
