import java.net.*;
import java.util.Scanner;

public class broadcastServer {

    public static void main(String[] args) {
        // Check the arguments
        if (args.length != 1) {
            System.out.println("usage: java UDPSender host port");
            return;
        }
        DatagramSocket socket = null;
        try {
            // Convert the arguments first, to ensure that they are valid
            InetAddress host = getBroadcast();
            int port = Integer.parseInt(args[0]);
            socket = new DatagramSocket();

            Scanner in;
            in = new Scanner(System.in);
            String message = null;
            while (true) {
                System.out.println("Enter text to be sent, ENTER to quit ");
                message = in.nextLine();
                if (message.length() == 0) break;
                byte[] data = message.getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length, host, port);
                socket.send(packet);
            }
            System.out.println("Closing down");
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (socket != null)
                socket.close();
        }
    }

    public static InetAddress getBroadcast() {
        try {
            InetAddress localHost = Inet4Address.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
            // for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
            //     System.out.println(address.getBroadcast());
            // }

            InterfaceAddress address = networkInterface.getInterfaceAddresses().get(0);
            return address.getBroadcast();
        } catch (Exception e) {
            return null;
        }
    }
}

