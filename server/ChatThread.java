import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatThread extends Thread {

    private Socket socket;
    private ArrayList<Socket> clients;
    private HashMap<Socket, String> clientNameList;

    public ChatThread(Socket socket, ArrayList<Socket> clients, HashMap<Socket, String> clientNameList) {
        this.socket = socket;
        this.clients = clients;
        this.clientNameList = clientNameList;
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String clientName = null;

            while (true) {
                String message = input.readLine();
                if (message == null || message.equals("logout")) {
                    break; // Exit the loop if the client disconnects or sends "logout"
                }

                if (clientName == null) {
                    // If clientName is not set, assume the first message is the client's name
                    clientName = message.split(":", 2)[0];
                    System.out.println(clientName + " has joined the chat room");
                    showMessageToAllClients(clientName + " has joined the chat room");
                    clientNameList.put(socket, clientName);
                } else {
                    System.out.println(clientName + ": " + message);
                    showMessageToAllClients(clientName + ": " + message);
                }
            }
        } catch (SocketException e) {
            handleClientDisconnect();
        } catch (IOException e) {
            System.err.println("Error reading from client: " + e.getMessage());
            handleClientDisconnect();
        }
    }

    private void handleClientDisconnect() {
        String clientName = clientNameList.get(socket);
        if (clientName != null) {
            System.out.println(clientName + " has left the chat room");
            showMessageToAllClients(clientName + " has left the chat room");
            clientNameList.remove(socket);
        }
        clients.remove(socket);
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }
    }

    private void showMessageToAllClients(String message) {
        synchronized (clients) {
            Iterator<Map.Entry<Socket, String>> iterator = clientNameList.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Socket, String> entry = iterator.next();
                Socket clientSocket = entry.getKey();
                PrintWriter printWriter;
                try {
                    printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                    printWriter.println(message);
                } catch (IOException e) {
                    System.err.println("Error sending message to client: " + e.getMessage());
                }
            }
        }
    }
}
