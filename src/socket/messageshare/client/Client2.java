package socket.messageshare.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client2 {
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5000);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // Start a thread to handle server messages
            Thread serverListener = new Thread(() -> {
                try {
                    while (true) {
                        String message = dataInputStream.readUTF();
                        System.out.println("[SERVER] " + message);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            });
            serverListener.start();

            while (true) {
                //System.out.print("input> ");
                String message = "client2: " + scanner.nextLine();
                dataOutputStream.writeUTF(message);
                if (message.equalsIgnoreCase("_stop"))
                    break;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
