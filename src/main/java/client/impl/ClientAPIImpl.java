package client.impl;

import client.ClientAPI;
import entities.User;
import security.CryptoSystem;
import security.RSA;
import util.ClientCommands;
import util.ServerCommands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by USER on 24.09.2016.
 */
public class ClientAPIImpl implements ClientAPI {
    private Socket socket;
    private User authenticated;

    public ClientAPIImpl(Socket socket) {
        this.socket = socket;
    }

    public void sendKeyAndReceiveSessionKey() {
        if (!isAuthenticated()) {
            System.out.println("You must sign in.");
            return;
        }

        KeyPair keyPair = genKeyPair();
        PublicKey publicKey = keyPair.getPublic();

        byte[] receivedSessionKey = new byte[0];
        try {
            sendRSAKey(publicKey);
            receivedSessionKey = receiveSessionEncodedKey();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        PrivateKey privateKey = keyPair.getPrivate();
    }

    private KeyPair genKeyPair() {
        RSA rsa = new RSA();
        rsa.initCipher(RSA.RSA);
        return rsa.generateKeyPair();
    }

    private void sendRSAKey(PublicKey key) throws IOException {
        OutputStream toServer = socket.getOutputStream();
        toServer.write(ClientCommands.SEND_PUBLIC_RSA_KEY.getValue());
        toServer.write(key.getEncoded());
        toServer.close();
    }

    private byte[] receiveSessionEncodedKey() throws IOException {
        InputStream fromServer = socket.getInputStream();
        byte[] encodedSessionKey = new byte[ServerCommands.SESSION_ENC_KEY_BYTE_LENGTH.getValue()];
        fromServer.read(encodedSessionKey);
        fromServer.close();
        return encodedSessionKey;
    }

    public void registerNewUser(User user) {

    }

    public void authenticateUser(String login, String pass) {

    }

    public void useKeyFromMail(String key) {

    }

    public void sendFilename(String filename) {

    }

    private boolean isAuthenticated() {
        return authenticated != null;
    }
}
