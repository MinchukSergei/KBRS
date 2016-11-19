package client.impl;

import client.ClientAPI;
import client.gui.ClientGui;
import dao.DAOuser;
import dao.impl.DAOuserImpl;
import entities.User;
import security.*;
import util.ClientCommands;
import util.CredentialMessage;
import util.ServerCommands;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Created by USER on 24.09.2016.
 */
public class ClientAPIImpl implements ClientAPI {
    private int attemptCount = 3;
    private Socket socket;
    private User authenticated;
    private byte[] sessionToken;
    private byte[] ksData;
    private String psw;
    private String ksPass;
    private PrivateKey privateDSKey;

    private InputStream fromServer;
    private OutputStream toServer;

    private ClientGui mainFrame;

    public PrivateKey getPrivateDSKey() {
        return privateDSKey;
    }

    public void setPrivateDSKey(PrivateKey privateDSKey) {
        this.privateDSKey = privateDSKey;
    }

    public String getKsPass() {
        return ksPass;
    }

    public void setKsPass(String ksPass) {
        this.ksPass = ksPass;
    }

    public byte[] getKsData() {
        return ksData;
    }

    public void setKsData(byte[] ksData) {
        this.ksData = ksData;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public ClientGui getMainFrame() {
        return mainFrame;
    }

    public byte[] getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(byte[] sessionToken) {
        this.sessionToken = sessionToken;
    }

    public void setFromServer(InputStream fromServer) {
        this.fromServer = fromServer;
    }

    public void setToServer(OutputStream toServer) {
        this.toServer = toServer;
    }

    public void setMainFrame(ClientGui mainFrame) {
        this.mainFrame = mainFrame;
    }

    public User getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(User authenticated) {
        this.authenticated = authenticated;
    }

    public ClientAPIImpl(Socket socket) {
        this.socket = socket;
    }

    public void sendKeyAndReceiveSessionKey() {
        if (socket == null) {
            JOptionPane.showMessageDialog(mainFrame, "Server is not available.");
            return;
        }
        KeyPair keyPair = genKeyPair();
        PublicKey publicKey = keyPair.getPublic();

        try {
            boolean res = sendRSAKey(publicKey);
            if (!res) {
                return;
            }
            mainFrame.getLogTextArea().append("Sending to server public RSA KEY\n");
            mainFrame.getLogTextArea().append("Receiving from server encoded session key\n");
        } catch (IOException e) {
            mainFrame.getLogTextArea().append(e.getMessage() + "\n");
            return;
        }
        DAOuser daOuser = new DAOuserImpl();
        authenticated.setUserPubKey(publicKey.getEncoded());

        String keyStorePass = "";
        try {
            byte[] ksData = receiveKSData();
            ksPass = getKSData(ksData, psw);

        } catch (IOException e) {
            e.printStackTrace();
        }
        PrivateKey privateKey = keyPair.getPrivate();
        try {
            KeyStoreUtils.writeEntry(authenticated.getUserLogin(), privateKey, ksPass);
        } catch (KeyStoreException e) {
            JOptionPane.showConfirmDialog(mainFrame, e.getMessage());
            return;
        } catch (CertificateException e) {
            JOptionPane.showConfirmDialog(mainFrame, e.getMessage());
            return;
        } catch (UnrecoverableEntryException e) {
            JOptionPane.showConfirmDialog(mainFrame, e.getMessage());
            return;
        } catch (NullPointerException e) {
            JOptionPane.showConfirmDialog(mainFrame, "KeyStore is invalid.");
            return;
        }

        try {
            mainFrame.getLogTextArea().append("Saving keys to database.\n");
            daOuser.setPubKey(authenticated);
        } catch (SQLException e) {
            JOptionPane.showConfirmDialog(mainFrame, e.getMessage());
            return;
        }
    }

    public String getKSData(byte[] ksData, String psw) {
        byte[] hashedPsw = SHA.hash(psw);
        byte[] res = new byte[32];
        for (int i = 0; i < hashedPsw.length; i++) {
            res[i] = (byte)(ksData[i] ^ hashedPsw[i]);
        }
        return Base64.encodeToBase64(res);
    }

    private KeyPair genKeyPair() {
        RSA rsa = new RSA();
        rsa.initCipher(RSA.RSA);
        return rsa.generateKeyPair();
    }

    private boolean sendRSAKey(PublicKey key) throws IOException {
        toServer.write(ClientCommands.SEND_PUBLIC_RSA_KEY.getValue());
        sendSessionToken(getSessionToken());
        int res = fromServer.read();
        if (res == ServerCommands.INCORRECT_TOKEN.getValue()) {
            JOptionPane.showMessageDialog(mainFrame, "Incorrect token.");
            return false;
        }
        if (res == ServerCommands.CORRECT_TOKEN.getValue()) {
            writeToServer(key.getEncoded());
            return true;
        }
        return false;
    }

    public byte[] receiveSessionEncodedKey() throws IOException {
        int result = fromServer.read();
        if (result == ServerCommands.PUBLIC_KEY_IS_NULL.getValue()) {
            return null;
        }
        byte[] encodedSessionKey = new byte[0];
        if (result == ServerCommands.PUBLIC_KEY_IS_CORRECT.getValue()) {
            encodedSessionKey = readFromServer();
        }
        return encodedSessionKey;
    }

    public boolean sendFilename(String filename) throws IOException {
        mainFrame.getLogTextArea().append("Sending to server filename.\n");

        if (authenticated.getUserPubKey().length == 0) {
            JOptionPane.showMessageDialog(mainFrame, "Generate new keys.");
            return false;
        }
        toServer.write(ClientCommands.SEND_FILENAME.getValue());
        sendSessionToken(getSessionToken());
        int res = fromServer.read();
        if (res == ServerCommands.INCORRECT_TOKEN.getValue()) {
            JOptionPane.showMessageDialog(mainFrame, "Incorrect token.");
            return false;
        }
        if (res == ServerCommands.CORRECT_TOKEN.getValue()) {
            toServer.write(filename.getBytes().length);
            toServer.write(filename.getBytes());
            return true;
        }
        return false;
    }

    public String receiveFile(String password) throws IOException, NoSuchAlgorithmException, SQLException, InvalidKeySpecException {
        mainFrame.getLogTextArea().append("Receiving from server file.\n");
        int command = fromServer.read();
        String file = "";
        ServerCommands target = ServerCommands.getCommandByValue(command);

        switch (target) {
            case SESSION_KEY_IS_NULL:
                JOptionPane.showMessageDialog(mainFrame, "Generate new session key.");
                return null;
            case FILE_EXISTS:
                file = receiveAndDecodeFile(authenticated.getUserLogin(), password);

                break;
            case FILE_NOT_FOUND:
                JOptionPane.showMessageDialog(mainFrame, "File not found");
                return null;
        }
        return file;
    }


//    public void sendDSPublicKey(PublicKey publicKey) throws IOException {
//        toServer.write(ClientCommands.SEND_DS_PUBLIC_KEY.getValue());
//        writeToServer(publicKey.getEncoded());
//    }

    public void sendCredentials(CredentialMessage credentialMessage) throws IOException {
        toServer.write(ClientCommands.SEND_CREDENTIALS.getValue());
        writeToServer(credentialMessage.getLogin().getBytes());
        writeToServer(credentialMessage.getPassword());
        writeToServer(credentialMessage.getSign());
    }

    private void writeToServer(byte[] msg) throws IOException {
        int byteArrayLength = 8;
        toServer.write(byteArrayLength);
        byte[] msgLength = ByteBuffer.allocate(byteArrayLength).putInt(msg.length).array();
        toServer.write(msgLength);
        toServer.write(msg);
    }

    private byte[] readFromServer() throws IOException {
        int byteArrayLength = fromServer.read();
        byte[] msgLengthBytes = new byte[byteArrayLength];
        fromServer.read(msgLengthBytes);
        int msgLength = ByteBuffer.wrap(msgLengthBytes).getInt();
        byte[] msg = new byte[msgLength];
        fromServer.read(msg);
        return msg;
    }

    public byte[] receiveSessionToken() throws IOException {
        byte[] token = readFromServer();
        return token;
    }

    public void sendSessionToken(byte[] sessionToken) throws IOException {
        writeToServer(sessionToken);
    }

    public ServerCommands receiveCredentialResult() throws IOException {
        int resultCredentials = fromServer.read();
        if (resultCredentials == ServerCommands.INCORRECT_CREDENTIALS.getValue()) {
            return ServerCommands.INCORRECT_CREDENTIALS;
        } else {
            int resultSign = fromServer.read();
            if (resultSign == ServerCommands.INCORRECT_SIGN.getValue()) {
                return ServerCommands.INCORRECT_SIGN;
            } else {
                return ServerCommands.CORRECT_SIGN;
            }
        }
    }

    public byte[] receiveKSData() throws IOException {
        return readFromServer();
    }

    private String receiveAndDecodeFile(String alias, String password) throws IOException, SQLException, NoSuchAlgorithmException {
        mainFrame.getLogTextArea().append("Encoding file from server.\n");

        PrivateKey privateKey = null;
        try {
            privateKey = KeyStoreUtils.readEntry(alias, password);
        } catch (KeyStoreException | CertificateException | UnrecoverableEntryException e) {
            toServer.write(ClientCommands.ERROR_FILE_RECEIVING.getValue());
            JOptionPane.showMessageDialog(mainFrame, e.getMessage());
            return null;
        }

        toServer.write(ClientCommands.CORRECT_FILE_RECEIVING.getValue());
        RSA rsa = new RSA();
        rsa.initCipher(CryptoSystem.RSA);
        AES aes = new AES();
        aes.initCipher(CryptoSystem.AES);
        byte[] encWithPubDSKeyFirstPart = readFromServer();
        byte[] encWithPubDSKeySecondPart = readFromServer();
        byte[] encFile = readFromServer();

        byte[] encWithPubKeyFirstPart = rsa.decode(encWithPubDSKeyFirstPart, privateDSKey);
        byte[] encWithPubKeySecondPart = rsa.decode(encWithPubDSKeySecondPart, privateDSKey);

        byte[] encWithPubKey = new byte[encWithPubKeyFirstPart.length + encWithPubKeySecondPart.length];
        System.arraycopy(encWithPubKeyFirstPart, 0, encWithPubKey, 0, encWithPubKeyFirstPart.length);
        System.arraycopy(encWithPubKeySecondPart, 0, encWithPubKey, encWithPubKeyFirstPart.length, encWithPubKeySecondPart.length);

        byte[] sessionKey = rsa.decode(encWithPubKey, privateKey);
        SecretKey sk = new SecretKeySpec(sessionKey, CryptoSystem.AES);
        String file = new String(aes.decode(encFile, sk));
        return file;
    }
}
