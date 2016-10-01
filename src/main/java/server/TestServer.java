package server;

import server.impl.ServerAPIImpl;
import util.ResourceBundleManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class TestServer {
    public static void main(String[] args) {
        int port = Integer.parseInt(ResourceBundleManager.getByName("server.port.number"));
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ServerAPIImpl serverAPI = new ServerAPIImpl(clientSocket);
                serverAPI.setFromClient(clientSocket.getInputStream());
                serverAPI.setToClient(clientSocket.getOutputStream());
                ClientThread clientThread = new ClientThread(serverAPI);
                Thread newClient = new Thread(clientThread);
                newClient.start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
