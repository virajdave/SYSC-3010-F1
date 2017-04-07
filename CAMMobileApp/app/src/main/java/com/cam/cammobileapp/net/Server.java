package com.cam.cammobileapp.net;

import android.util.Log;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;

public class Server {
    protected static final int PACKET_SIZE = 1500;

    private DatagramSocket socket;
    private Integer port;
    private InetAddress bcast = null;
    private InetSocketAddress addr;

    public Server() {
        addr = new InetSocketAddress("10.0.0.1", 3010);

        try {
            socket = new DatagramSocket();
            port = socket.getLocalPort();
            Log.i("Server", "using port " + port);
        } catch (SocketException e) {
            if (e.getMessage().toLowerCase().equals("socket closed") && Thread.interrupted()) {
                return;
            }
            Log.e("Server", "socket error", e);
        }
    }

    public void stop() {
        socket.close();
    }

    /**
     * Send and then wait to receive.
     *
     * @param message
     * @param timeout
     * @return
     */
    public String request(String message, int timeout) {
        send(message);
        return recv(timeout);
    }

    /**
     * Send a message over broadcast.
     *
     * @param message
     */
    public void send(String message) {
        if (bcast == null) {
            // Get the bcast address if it hasn't been set yet.
            bcast = Net.getBroadcast();
            if (bcast != null) {
                Log.i("Net", "broadcast set to " + bcast.toString());
            } else {
                Log.i("Net", "could not get broadcast");
                return;
            }
        }

        try {
            if (socket != null) {
                // Convert message data to bytes, create the packet and send it.
                byte[] sendData = message.getBytes("UTF-8");
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, bcast, addr.getPort());
                socket.send(sendPacket);
                Log.i("Server",  "b.sent -> " + message);
            } else {
                Log.d("Server", "not running, cannot send message.");
            }
        } catch (IOException e) {
            Log.e("Server", "send error", e);
        }
    }

    /**
     * Wait to receive a message.
     *
     * @param timeout
     * @return message or null on timeout
     */
    public String recv(int timeout) {
        try {
            socket.setSoTimeout(timeout);

            byte[] data = new byte[PACKET_SIZE];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            socket.receive(packet);

            return new String(packet.getData(), 0, packet.getLength());
        } catch (SocketException e) {
            Log.e("Server", "recv error", e);
        } catch (IOException e) {
            Log.e("Server", "recv errora", e);
        }
        return null;
    }

    public Integer getPort() {
        return port;
    }

    public InetSocketAddress getAddr() {
        return addr;
    }

    public void setAddr(InetSocketAddress address) {
        addr = address;
    }

    public abstract class MyRunnable implements Runnable {
        protected String data;
        public MyRunnable(String data) {
            this.data = data;
        }
    }
}