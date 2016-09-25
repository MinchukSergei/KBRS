package client;

import client.gui.ClientGui;
import client.impl.ClientAPIImpl;
import entities.User;
import security.AES;
import security.CryptoSystem;
import security.RSA;
import util.ClientCommands;
import util.ResourceBundleManager;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public  class TestClient {
    static User user;
    private static SecretKey encodePrivateKey(PrivateKey key) {
        AES aes = new AES();
        aes.initCipher(CryptoSystem.AES);
        SecretKey secretKey = aes.generateKey();
        byte[] encodedKey = aes.encode(key.getEncoded(), secretKey);
        user.setUserPrKey(encodedKey);
        return secretKey;
    }

    public static void main(String[] args) {
        Socket socket;
        ClientAPIImpl clientAPI = null;
        try {
            socket = new Socket(
                    ResourceBundleManager.getByName("server.host.address"),
                    Integer.parseInt(ResourceBundleManager.getByName("server.port.number"))
                    );
            clientAPI = new ClientAPIImpl(socket);
            clientAPI.setFromServer(socket.getInputStream());
            clientAPI.setToServer(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        ClientGui clientGui = new ClientGui(clientAPI);
        clientAPI.setMainFrame(clientGui);
        clientGui.setDefaultSettings();
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//        try {
//            String line = bufferedReader.readLine();
//        } catch (IOException e) {
//
//        }

        //reader.skip(10);

        //System.out.println(password);

//        byte[] out = null;
//        RSA rsa = new RSA();
//        rsa.initCipher(CryptoSystem.RSA);
//        KeyPair pair = rsa.generateKeyPair();
//        PrivateKey pk = pair.getPrivate();
//
//        user = new User();
//        user.setUserLogin("pes");
//        user.setUserEmail("minchuk94@mail.ru");
//        SecretKey key = encodePrivateKey(pk);
//
//        MailSender mailSender = new MailSender();
//
//        try {
//            Message msg = mailSender.createMessage(user.getUserEmail(), "test key",
//                    Base64.encodeToBase64(key.getEncoded()));
//            mailSender.sendMessage(msg);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//
//        DAOuser daOuser = new DAOuserImpl();
//        try {
//            daOuser.setPrKey(user);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        user.setUserPassword("gau");



//        AES aes = new AES();
//        aes.initCipher(AES.AES);
//        SecretKey sessKey = aes.generateKey();
//        String b64encodedKey = security.Base64.encodeToBase64(sessKey.getEncoded());
//        SecretKey key = aes.generateKey();
//        byte[] encKey = aes.encode(b64encodedKey, key);
//        String encodedKey = security.Base64.encodeToBase64(encKey);
//        int y = encodedKey.length();
//        user.setUserKey(encodedKey);
//        try {
//            daOuser.setPrKey(user);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }



    }
}
