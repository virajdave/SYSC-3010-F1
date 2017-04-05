import java.util.ArrayList;
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
		ArrayList<Integer> removed = new ArrayList<>(devices.keySet());
		int currentSize = devices.size();
		int currentIndex = 0;
		int firstChange = -1;
		int numberAdded = 0;
		
		for (String dev : list) {
			if (dev.length() != 0) {
				String info[] = dev.split(":");
				int id = Parse.toInt(info[0]);
				int type = Parse.toInt(info[1]);
				boolean dead = Parse.toBool(info[2]);

				DeviceInfo curinfo = devices.get(id);
				DeviceInfo newInfo = new DeviceInfo(type, dead);
				devices.put(id, newInfo);

				if (curinfo == null) {
					if (firstChange == -1 || currentIndex < firstChange) {
						firstChange = currentIndex;
					}
					numberAdded++;
				} else {
					removed.remove((Integer)id);
					if (!curinfo.equals(newInfo)) {
						if (firstChange == -1 || currentIndex < firstChange) {
							fireContentsChanged(this, currentIndex, currentIndex);
						}
					}
				}
				currentIndex++;
			}
		}

		// Remove any remaining entries that were not in the input list.
		for (int i = 0; i < removed.size(); i++) {
			devices.remove(removed.get(i));
			numberAdded--;
		}
		
		// Properly notify how many elements were changed/added/removed.
		if (firstChange != -1) {
			if (numberAdded > 0) {
				System.out.println("Changed: [" + firstChange + ", " + (currentSize) + "] Added: [" + currentSize + ", " + (currentSize + numberAdded) + "]");
				fireContentsChanged(this, firstChange, currentSize - 1);
				fireIntervalAdded(this, currentSize, currentSize + numberAdded);
			} else if (numberAdded < 0) {
				System.out.println("Changed: [" + firstChange + ", " + (currentSize + numberAdded) + "] Removed: [" + (currentSize + numberAdded) + ", " + (currentSize) + "]");
				fireContentsChanged(this, firstChange, currentSize + numberAdded);
				fireIntervalRemoved(this, currentSize + numberAdded, currentSize);
			} else {
				System.out.println("Changed: [" + firstChange + ", " + (currentSize) + "]");
				fireContentsChanged(this, firstChange, currentSize);
			}
		} else {
			if (numberAdded > 0) {
				System.out.println("Added: [" + currentSize + ", " + (currentSize + numberAdded) + "]");
				fireIntervalAdded(this, currentSize, currentSize + numberAdded);
			} else if (numberAdded < 0) {
				System.out.println("Removed: [" + (currentSize + numberAdded) + ", " + (currentSize) + "]");
				fireIntervalRemoved(this, currentSize + numberAdded, currentSize);
			} else {
				System.out.println("No change");
			}
		}
	}

	@Override
	public String getElementAt(int index) {
		Integer id = (Integer) devices.keySet().toArray()[index];
		DeviceInfo info = devices.get(id);
		
		if (info != null) {
			String str = "#" + id;
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
        "Bed"
    };

}
