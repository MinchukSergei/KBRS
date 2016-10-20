package server;

import security.CryptoSystem;
import security.RSA;
import server.impl.ServerAPIImpl;
import util.ResourceBundleManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.text.ParseException;


public class TestServer {


    public static void main(String[] args) throws ParseException {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        File file = new File(loader.getResource("KeyStore.jks").getFile());
        String alias = "alias";
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(new FileInputStream(file), "password".toCharArray());

            KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
                    ks.getEntry("alias", new KeyStore.PasswordProtection("password".toCharArray()));


            ks.deleteEntry("secretKeyAlias");

            FileOutputStream fos = new FileOutputStream(file);
            ks.store(fos, "password".toCharArray());
            fos.close();



            PrivateKey myPrivateKey = pkEntry.getPrivateKey();

            RSA rsa = new RSA();
            rsa.initCipher(CryptoSystem.RSA);
            KeyPair kp = rsa.generateKeyPair();
            PrivateKey mySecretKey = kp.getPrivate();


            KeyStore.PrivateKeyEntry prEntry =
                    new KeyStore.PrivateKeyEntry(mySecretKey, new Certificate[] {ks.getCertificate(alias)});
            ks.setEntry(alias, prEntry, new KeyStore.PasswordProtection("password".toCharArray()));

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        }


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
