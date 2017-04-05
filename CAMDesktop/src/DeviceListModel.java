import java.util.TreeMap;

import javax.swing.AbstractListModel;

import util.Parse;

public class DeviceListModel extends AbstractListModel<String> {
	private static final long serialVersionUID = 1L;
	private TreeMap<Integer, DeviceInfo> devices;
	
	public DeviceListModel() {
		devices = new TreeMap<>();
	}
	
	public void setElements(String[] list) {
		for (String dev : list) {
			if (dev.length() != 0) {
				String info[] = dev.split(":");
				int id = Parse.toInt(info[0]);
				int type = Parse.toInt(info[1]);
				boolean dead = Parse.toBool(info[2]);
				
				devices.put(id, new DeviceInfo(type, dead));
			}
		}
	}

	@Override
	public String getElementAt(int index) {
		DeviceInfo info = devices.get(index);
		
		if (info != null) {
			String str = "#" + index;
			str += " " + TYPENAMES[info.type];
			if (info.disconnected)
				str += " (Disconnected)";
			
			return str;
		}
		return null;
	}

	@Override
	public int getSize() {
		return devices.size();
	}
	
	public boolean isEmpty() {
		return devices.isEmpty();
	}
	
	private class DeviceInfo {
		public int type;
		public boolean disconnected;
		
		public DeviceInfo(int type, boolean disconnected) {
			this.type = type;
			this.disconnected = disconnected;
		}
	}
    
    static final String[] TYPENAMES = new String[] {
        "Lights",
        "Switch",
        "Mirror",
        "Thermostat",
        "Bed"
    };

}
