package com.example.benjamincroskery.udp.util;

import android.util.Log;

import java.net.*;
import java.util.Collections;

public class Net {

    /**
     * Get the IP Address of this device.
     * @return InetAddress Address
     */
    public static InetAddress getIPAddress() {
        try {
            for (NetworkInterface netInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress address : Collections.list(netInterface.getInetAddresses())) {
                    // Use the first valid IPv4 non-loopback address available.
                    if (!address.isLoopbackAddress()) {
                        String sAddr = address.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') == -1;

                        if (isIPv4) {
                            return address;
                        }
                    }
                }
            }
        }  catch (Exception e) {
            Log.e("FAIL", "net", e);
        } // EAT exceptions.
        return null;
    }

    /**
     * Get the Broadcast Address for the subnet.
     * @return InetAddress Address
     */
    public static InetAddress getBroadcast() {
        try {
            InetAddress localHost = getIPAddress();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
            for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                // Use the first broadcast address available.
                if (address.getBroadcast() != null) {
                    return address.getBroadcast();
                }
            }
        } catch (Exception e) {
            Log.e("FAIL", "net", e);
        } // EAT exceptions.
        return null;
    }

}
