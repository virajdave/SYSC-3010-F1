package com.cam.cammobileapp;

import android.util.Log;

import com.cam.cammobileapp.Net;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;

public class ServerOnApp extends Thread {
    protected static final int PACKET_SIZE = 1500;

    private Queue<String> recvQueue;
    private DatagramSocket socket;
    private Integer port;
    private InetAddress bcast = null;
    private InetSocketAddress addr;

    public ServerOnApp() {
        recvQueue = new LinkedList<>();
        socket = null;
        addr = new InetSocketAddress("10.0.0.1", 3010);
    }

    /**
     * Start up the server.
     */
    public void run() {

        try {
            // Create socket.
            socket = new DatagramSocket();
            port = socket.getLocalPort();
            Log.i("Server",  "using port " + port);

            // Receive.
            while (!Thread.interrupted()) {
                // Stick a received message in a packet.
                byte[] data = new byte[PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                // Create a message from the packet and add it to the queue,
                // notify any waiting receives.
                String message = new String(packet.getData(), 0, packet.getLength());
                Log.i("Server",  "recv -> " + message);
                synchronized (recvQueue) {
                    recvQueue.add(message);
                    recvQueue.notifyAll();
                }
            }

        } catch (SocketException e) {
            if (e.getMessage().toLowerCase().equals("socket closed") && Thread.interrupted()) {
                return;
            }
            Log.e("Server", "socket error", e);
        } catch (IOException e) {
            Log.e("Server", "socket error", e);
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    /**
     * Close down the server.
     */
    @Override
    public void interrupt() {
        super.interrupt();
        socket.close();
        Log.i("Server", "interrupted");
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {

        new Thread(new MyRunnable(message) {
            public void run() {
                try {
                    if (socket != null) {
                        // Convert message data to bytes, create the packet and send it.
                        byte[] sendData = data.getBytes("UTF-8");
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, addr);
                        socket.send(sendPacket);
                        Log.i("Server",  "sent -> " + data);
                    } else {
                        Log.d("Server", "not running, cannot send message.");
                    }
                } catch (IOException e) {
                    Log.e("Server", "send error", e);
                }
            }
        }).start();
    }

    /**
     * Send a message over broadcast.
     *
     * @param message
     */
    public void sendBroadcast(String message) {

        new Thread(new MyRunnable(message) {
            public void run() {
                if (bcast == null) {
                    // Get the bcast address if it hasn't been set yet.
                    bcast = Net.getBroadcast();
                    if (bcast != null) {
                        Log.i("Net", "broadcast set to " + bcast.toString());
                    } else {
                        Log.i("Net", "could not get broacast");
                        return;
                    }
                }

                try {
                    if (socket != null) {
                        // Convert message data to bytes, create the packet and send it.
                        byte[] sendData = data.getBytes("UTF-8");
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, bcast, addr.getPort());
                        socket.send(sendPacket);
                        Log.i("Server",  "b.sent -> " + data);
                    } else {
                        Log.d("Server", "not running, cannot send message.");
                    }
                } catch (IOException e) {
                    Log.e("Server", "send error", e);
                }
            }
        }).start();
    }

    /**
     * Check for a received message, waiting if the queue is empty.
     *
     * @return Message
     */
    public String recvWait(int timeout) {
        // Wait if the queue is empty.
        synchronized (recvQueue) {
            if (recvQueue.isEmpty()) {
                try {
                    recvQueue.wait(timeout);
                    return recvQueue.poll();
                } catch (InterruptedException e) {
                }
            }
            return recvQueue.poll();
        }
    }

    /**
     * Check for a received message.
     *
     * @return Message or null if the queue is empty
     */
    public String recvMessage() {
        // Grab the first message and return it.
        synchronized (recvQueue) {
            return recvQueue.poll();
        }
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