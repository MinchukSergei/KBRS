package server;

import org.apache.commons.codec.digest.Crypt;
import security.AES;
import security.CryptoSystem;
import server.impl.ServerAPIImpl;
import util.ResourceBundleManager;

import javax.crypto.SecretKey;
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
//        Path currentRelativePath = Paths.get("");
//        String s = currentRelativePath.toAbsolutePath().toString();
//        File file = new File(s + "\\testFiles\\1.txt");
//        AES aes = new AES();
//        aes.initCipher(CryptoSystem.AES);
//        SecretKey key = aes.generateKey();
//
//        if (file.exists()) {
//            InputStream fileStream = null;
//            try {
//                fileStream = new FileInputStream(file);
//
//            byte[] partFile = new byte[128];
//            while (fileStream.read(partFile) != -1) {
//                byte[] encPart = aes.encode(partFile, key);
//                int y = 0;
//            }
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }


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
