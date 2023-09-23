package socket.messageshare.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static List<ClientHandler> clients = new ArrayList<>(); // this will store the list of connected clients
    // this will be used to broadcast messages to all connected clients.

    public static void main(String[] args) {
        try {
            // Starting a server socket
            ServerSocket serverSocket = new ServerSocket(5000);
            System.out.println("Listening to port 5000");

            // Accepting client connections. This is a blocking call. It will wait until a client connects
            while (true) {
                Socket clientSocket = serverSocket.accept(); // it will create a new socket for each client
                System.out.println(clientSocket + " connected\n"); // printing the client socket

                // Create a new thread to handle this client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler); // adding the client to the list of connected clients
                clientHandler.start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}


// This class will handle each client connection. It will run in a separate thread.
// so that it can handle multiple clients at the same time.
class ClientHandler extends Thread {
    private final Socket clientSocket; // this will store the client socket
    private DataOutputStream dataOutputStream;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket; // storing the client socket
    }

    @Override
    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

            String message;
            while(true) {
                message = dataInputStream.readUTF(); // reading the message from the client
                System.out.println(message); // printing the message

                // Broadcast the message to all connected clients
                broadcast(message);

                // exit condition
                if (message.equalsIgnoreCase("_stop"))
                    break;
            }

            clientSocket.close(); // closing the client socket
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // this method will be used to broadcast messages to all connected clients
    private void broadcast(String message) {
        // we need to synchronize the clients list because multiple threads will be accessing it
        synchronized (Server.clients) {
            // accessing each client from the list and sending the message
            for (ClientHandler client : Server.clients) {
                try {
                    client.dataOutputStream.writeUTF(message); // sending the message to the client
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }
}

