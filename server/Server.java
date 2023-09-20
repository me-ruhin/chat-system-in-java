import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {

    public static void main(String[] args) {
        ArrayList<Socket> connectedClients = new ArrayList<>();
        HashMap<Socket, String> clientUsernameMap = new HashMap<>();
        try (ServerSocket serverSocket = new ServerSocket(5054)) {
            System.out.println("Chat server is started...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                connectedClients.add(clientSocket);
                ChatThread chatThread = new ChatThread(clientSocket, connectedClients, clientUsernameMap);
                chatThread.start();
            }
        } catch (Exception e) {
            // Handle exceptions here
            e.printStackTrace();
        }
    }
}
