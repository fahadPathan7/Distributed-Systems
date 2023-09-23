package socket.messageshare.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client2 {
    private static DataInputStream dataInputStream = null;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // Starting a thread to handle server messages
            Thread serverListener = new Thread(() -> {
                try {
                    while (true) {
                        String message = dataInputStream.readUTF(); // reading the message from the server
                        System.out.println("[FROM SERVER] " + message); // printing the message from the server
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            });
            serverListener.start(); // starting the thread

            while (true) {
                // System.out.print("\n input> ");
                String message = "client2: " + scanner.nextLine(); // reading the message from the client
                dataOutputStream.writeUTF(message); // sending the message to the server

                // condition to stop the client
                if (message.equalsIgnoreCase("_stop")) break;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
