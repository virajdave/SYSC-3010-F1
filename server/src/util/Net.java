package util;

import java.net.*;
import java.util.Collections;

public class Net {
	
	public static InetAddress getIPAddress() {
        try {
            for (NetworkInterface netInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress address : Collections.list(netInterface.getInetAddresses())) {
                    if (!address.isLoopbackAddress()) {
//                        String sAddr = address.getHostAddress();
//                        boolean isIPv4 = sAddr.indexOf(':') == -1;
//
//                        if (isIPv4)
                            return address;
                    }
                }
            }
        } catch (Exception ex) {} // EAT exceptions.
        return null;
    }

    public static InetAddress getBroadcast() {
        try {
            InetAddress localHost = getIPAddress();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
//          for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
//          System.out.println(address.getBroadcast());
//      }

            InterfaceAddress address = networkInterface.getInterfaceAddresses().get(0);
            return address.getBroadcast();
        } catch (Exception e) {} // EAT exceptions.
		return null;
	}

}
