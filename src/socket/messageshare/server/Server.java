package socket.messageshare.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static List<ClientHandler> clients = new ArrayList<>(); // this will store

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Listening to port 5000");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(clientSocket + " connected\n");

                // Create a new thread to handle this client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

class ClientHandler extends Thread {
    private Socket clientSocket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

            String message;
            while (true) {
                message = dataInputStream.readUTF();
                System.out.println(message);

                // Broadcast the message to all connected clients
                broadcast(message);

                if (message.equalsIgnoreCase("_stop"))
                    break;
            }

            clientSocket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void broadcast(String message) {
        synchronized (Server.clients) {
            for (ClientHandler client : Server.clients) {
                try {
                    client.dataOutputStream.writeUTF(message);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }
}

