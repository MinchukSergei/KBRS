package server;

import org.apache.commons.codec.digest.Crypt;
import security.AES;
import security.CryptoSystem;
import server.impl.ServerAPIImpl;
import util.ResourceBundleManager;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by USER on 21.09.2016.
 */
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
