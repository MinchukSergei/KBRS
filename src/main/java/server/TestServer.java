package server;

import security.CryptoSystem;
import security.KeyStoreUtils;
import security.RSA;
import server.impl.ServerAPIImpl;
import sun.security.rsa.RSAPrivateCrtKeyImpl;
import util.ResourceBundleManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.util.Arrays;
import java.util.BitSet;


public class TestServer {


    public static void main(String[] args) throws ParseException, IOException {

//        File roots = File.listRoots()[3];
//
//        FileOutputStream fids = new FileOutputStream(roots.toString() + "KBRS");
//        fids.write(new byte[]{5});
//        fids.close();
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
