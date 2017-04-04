package com.example.benjamincroskery.udp;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;

public class Utils {

    /**
     * Get IPv4 address from first non-localhost interface
     * @return  address or empty string
     */
    public static String getIPAddress() {
        try {
            for (NetworkInterface netInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress address : Collections.list(netInterface.getInetAddresses())) {
                    if (!address.isLoopbackAddress()) {
                        String sAddr = address.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') == -1;

                        if (isIPv4)
                            return sAddr;
                    }
                }
            }
        } catch (Exception ex) {} // EAT exceptions.
        return "";
    }

}
