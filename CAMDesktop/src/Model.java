import java.net.InetSocketAddress;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import main.Server;
import types.Message;
import util.Codes;
import util.Parse;

public class Model extends Observable {
	private static final int TIMEOUT = 1000;
	private static final int NET_RATE = 5000;
	private static final InetSocketAddress serverAddr = new InetSocketAddress("localhost", 3010);

	private Server s;
	private DeviceListModel devices;
	private ExecutorService executor;

	public Model(DeviceListModel devices) {
		this.s = new Server();
		s.start();
		this.devices = devices;
		this.executor = Executors.newFixedThreadPool(2);
	}

	public void start() {
		this.setChanged();
		this.notifyObservers("Loading...");
		
		executor.submit(new Runnable() {
			@Override
			public void run() {
				while (!Thread.interrupted()) {
					// Update the net info.
					executor.submit(new Runnable() {
						@Override
						public void run() {
							updateNetInfo();
						}
					});
					
					try {
						Thread.sleep(NET_RATE);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void selectDevice(int index) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				getDeviceInfo(devices.getIDAt(index));
			}
		});
	}

	public void deleteDevice(int index) {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				s.sendMessage(new Message(Parse.toString("/", Codes.W_APP + "" + Codes.T_DELETE, devices.getIDAt(index)), serverAddr));
			}
		});
	}

	private void updateNetInfo() {
		s.sendMessage(new Message(Parse.toString("", Codes.W_APP, Codes.T_NETINF), serverAddr));
		Message msg = s.recvWait(TIMEOUT);
		if (msg == null) {
			this.setChanged();
			this.notifyObservers("Server disconnected.");
		}

		if (msg.getMessage().substring(0, 2).equals(Parse.toString("", Codes.W_SERVER, Codes.T_NETINF))) {
			devices.setElements(msg.getMessage().substring(3).split("/"));
		}
		
		this.setChanged();
		this.notifyObservers("");
	}

	private void getDeviceInfo(int id) {
		s.sendMessage(new Message(Parse.toString("/", Codes.W_APP + "" + Codes.T_DEVINF, id), serverAddr));
		Message msg = s.recvWait(TIMEOUT);
		if (msg == null) {
			this.setChanged();
			this.notifyObservers("[Server did not respond.");
		}
		
		if (msg.getMessage().substring(0, 2).equals(Parse.toString("", Codes.W_SERVER, Codes.T_DEVINF))) {
			
			this.setChanged();
			this.notifyObservers("[" + msg.getMessage().substring(3).replace('/', '\n'));
		}
	}
}
