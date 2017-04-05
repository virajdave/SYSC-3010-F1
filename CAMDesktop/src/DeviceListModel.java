import java.util.Iterator;
import java.util.Map.Entry;
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
		// Get the current ids so that stray entries can be removed later.
		TreeMap<Integer, DeviceInfo> oldList = devices;
		devices = new TreeMap<>();
		
		for (String dev : list) {
			if (dev.length() != 0) {
				String info[] = dev.split(":");
				int id = Parse.toInt(info[0]);
				int type = Parse.toInt(info[1]);
				boolean dead = Parse.toBool(info[2]);
				
				devices.put(id, new DeviceInfo(type, dead));
			}
		}
		
		int index = 0;
		boolean[] changed = new boolean[oldList.size()];
		Iterator<Entry<Integer, DeviceInfo>> newSet = devices.entrySet().iterator();
		for (Entry<Integer, DeviceInfo> oldDev : oldList.entrySet()) {
			if (!newSet.hasNext()) {
				break;
			}
			Entry<Integer, DeviceInfo> newDev = newSet.next();
			
			changed[index++] = !oldDev.equals(newDev);
		}
		
		int i = 0;
		while (i < index) {
			if (changed[i]) {
				System.out.println("a");
				int start = i;
				while (++i < index && changed[i]);
				System.out.println("b");
				int end = i - 1;
				System.out.println("Changed: " + start + ", " + end);
				fireContentsChanged(this, start, end);
			}
			i++;
		}
		
		if (devices.size() < oldList.size()) {
			System.out.println("Removed: " + devices.size() + ", " + (oldList.size() - 1));
			fireIntervalRemoved(this, devices.size(), oldList.size() - 1);
		} else if (devices.size() > oldList.size()) {
			System.out.println("Added: " + oldList.size() + ", " + (devices.size() - 1));
			fireIntervalAdded(this, oldList.size(), devices.size() - 1);
		}
	}

	@Override
	public String getElementAt(int index) {
		Integer id = (Integer) devices.keySet().toArray()[index];
		DeviceInfo info = devices.get(id);
		
		if (info != null) {
			String str = "#" + id;
			if (info.type >= TYPENAMES.length) {
				info.type = TYPENAMES.length - 1;
			}
			str += " " + TYPENAMES[info.type];
			if (info.disconnected)
				str += " (Disconnected)";
			
			return str;
		}
		return null;
	}

	public Integer getIDAt(int index) {
		return (Integer) devices.keySet().toArray()[index];
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
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof DeviceInfo) {
				DeviceInfo d = (DeviceInfo)o;
				return type == d.type && disconnected == d.disconnected;
			}
			return false;
		}
	}
    
    static final String[] TYPENAMES = new String[] {
        "Lights",
        "Switch",
        "Mirror",
        "Thermostat",
        "Bed",
        "Null"
    };

}
