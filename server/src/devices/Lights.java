package devices;

import types.Conf;
import types.Data;
import types.Info;

public class Lights extends Device {

	@Override
	public void giveMessage(String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean giveInput(Data in) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setConf(Conf in) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean requestOutput(Data in) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Info getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
