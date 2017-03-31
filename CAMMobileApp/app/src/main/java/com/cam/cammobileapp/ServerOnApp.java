package com.cam.cammobileapp;

/**
 * Created by virajdave on 2017-03-31.
 */


        import java.io.IOException;
        import java.net.*;
        import java.util.LinkedList;
        import java.util.Queue;
        import java.util.Scanner;

public class ServerOnApp extends Thread {
    protected static final int PACKET_SIZE = 1500;

    private Queue<String> recvQueue;
    private DatagramSocket socket;
    private Integer port;
    private InetAddress bcast = null;
    private static final InetSocketAddress socketAddress = new InetSocketAddress("10.0.0.0", 3010);
    public ServerOnApp() {
        this(null);
    }

    public ServerOnApp(Integer _port) {
        recvQueue = new LinkedList<>();
        socket = null;
        port = _port;
    }

    /**
     * Start up the server.
     */
    public void run() {
        try {
            // Create socket.
            if (port != null) {
                socket = new DatagramSocket(port);
                // socket.setReuseAddress(true);
                // InetSocketAddress t = new InetSocketAddress(UDP_SERVER_PORT);
                // socket.bind(t);
            } else {
                socket = new DatagramSocket();
                port = socket.getLocalPort();
            }

            // Receive.
            while (!Thread.interrupted()) {
                // Stick a received message in a packet.
                byte[] data = new byte[PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(data, data.length);
                socket.receive(packet);
                // Create a message from the packet and add it to the queue,
                // notify any waiting receives.
                String message = new String(packet.getData(), 0, packet.getLength());
                synchronized (recvQueue) {
                    recvQueue.add(message);
                    recvQueue.notifyAll();
                }
            }

        } catch (SocketException e) {
            if (e.getMessage().toLowerCase().equals("socket closed") && Thread.interrupted()) {
                return;
            }
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
        try {
            if (socket != null) {
                // Convert message data to bytes, create the packet and send it.
                byte[] sendData = message.getBytes("UTF-8");
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, socketAddress);
                socket.send(sendPacket);
            } else {
                System.err.println("Server not running, cannot send message.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Check for a received message, waiting if the queue is empty.
     *
     * @return Message
     */
    public String recvWait() {
        // Wait if the queue is empty.
        synchronized (recvQueue) {
            if (recvQueue.isEmpty()) {
                try {
                    recvQueue.wait();
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


}
