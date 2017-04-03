import java.net.InetSocketAddress;
import java.util.Observable;

import main.Server;
import types.Message;
import util.Codes;
import util.Parse;

public class Model extends Observable {
	private static final InetSocketAddress serverAddr = new InetSocketAddress("localhost", 3010);

	private Server s;

	public Model() {
		s = new Server();
	}

	public void updateNetInfo() {
		s.sendMessage(new Message(Parse.toString("", Codes.W_APP, Codes.T_NETINF), serverAddr));
		Message msg = s.recvWait();
		this.setChanged();
		this.notifyObservers(msg.toString());
	}

	public void getDeviceInfo(int id) {
		s.sendMessage(new Message(Parse.toString("/", Codes.W_APP + "" + Codes.T_DEVINF, id), serverAddr));
		Message msg = s.recvWait();
		this.setChanged();
		this.notifyObservers(msg.toString());
	}
}
