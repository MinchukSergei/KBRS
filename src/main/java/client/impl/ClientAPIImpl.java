package client.impl;

import client.ClientAPI;
import client.gui.ClientGui;
import dao.DAOuser;
import dao.impl.DAOuserImpl;
import entities.User;
import security.*;
import util.ClientCommands;
import util.ServerCommands;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

/**
 * Created by USER on 24.09.2016.
 */
public class ClientAPIImpl implements ClientAPI {
    private Socket socket;
    private User authenticated;
    private String emailKeyB64;

    private InputStream fromServer;
    private OutputStream toServer;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public InputStream getFromServer() {
        return fromServer;
    }

    public void setFromServer(InputStream fromServer) {
        this.fromServer = fromServer;
    }

    public OutputStream getToServer() {
        return toServer;
    }

    public void setToServer(OutputStream toServer) {
        this.toServer = toServer;
    }

    public ClientGui getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(ClientGui mainFrame) {
        this.mainFrame = mainFrame;
    }

    private ClientGui mainFrame;

    public String getEmailKeyB64() {
        return emailKeyB64;
    }

    public void setEmailKeyB64(String emailKeyB64) {
        this.emailKeyB64 = emailKeyB64;
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

    public void sendKeyAndReceiveSessionKey(String keyStorePass) {
        if (socket == null) {
            JOptionPane.showMessageDialog(mainFrame, "Server is not available.");
            return;
        }
        KeyPair keyPair = genKeyPair();
        PublicKey publicKey = keyPair.getPublic();


        byte[] receivedSessionKey = new byte[0];
        try {
            sendRSAKey(publicKey);
            mainFrame.getLogTextArea().append("Sending to server public RSA KEY\n");
            receivedSessionKey = receiveSessionEncodedKey();
            mainFrame.getLogTextArea().append("Receiving from server encoded session key\n");
        } catch (IOException e) {
            mainFrame.getLogTextArea().append(e.getMessage() + "\n");
            return;
        }
        DAOuser daOuser = new DAOuserImpl();
        if (receivedSessionKey == null) {
            JOptionPane.showConfirmDialog(mainFrame, "Session key wasn't received.");
            return;
        }
        authenticated.setUserSessionKey(receivedSessionKey);
        authenticated.setUserPubKey(publicKey.getEncoded());

        PrivateKey privateKey = keyPair.getPrivate();
        JOptionPane.showConfirmDialog(mainFrame, "Saving pr key to key store.");
        try {
            KeyStoreUtils.writeEntry(authenticated.getUserLogin(), privateKey, keyStorePass);
        } catch (KeyStoreException e) {
            JOptionPane.showConfirmDialog(mainFrame, e.getMessage());
        } catch (CertificateException e) {
            JOptionPane.showConfirmDialog(mainFrame, e.getMessage());
        } catch (UnrecoverableEntryException e) {
            JOptionPane.showConfirmDialog(mainFrame, e.getMessage());
        } catch (NullPointerException e) {
            JOptionPane.showConfirmDialog(mainFrame, "KeyStore is invalid.");
        }

        try {
            mainFrame.getLogTextArea().append("Saving keys to database.\n");
            daOuser.setSessionKey(authenticated);
            daOuser.setPubKey(authenticated);
            daOuser.setPrKey(authenticated);
        } catch (SQLException e) {
            JOptionPane.showConfirmDialog(mainFrame, e.getMessage());
            return;
        }
    }

    private KeyPair genKeyPair() {
        RSA rsa = new RSA();
        rsa.initCipher(RSA.RSA);
        return rsa.generateKeyPair();
    }

    private void sendRSAKey(PublicKey key) throws IOException {
        toServer.write(ClientCommands.SEND_PUBLIC_RSA_KEY.getValue());
        toServer.write(key.getEncoded());
    }

    private byte[] receiveSessionEncodedKey() throws IOException {
        byte[] encodedSessionKey = new byte[ServerCommands.SESSION_ENC_KEY_BYTE_LENGTH.getValue()];
        int n = fromServer.read(encodedSessionKey);
        if (n < encodedSessionKey.length) {
            fromServer.close();
            return null;
        }
        return encodedSessionKey;
    }

    public boolean sendFilename(String filename) throws IOException {
        mainFrame.getLogTextArea().append("Sending to server filename.\n");
        if (emailKeyB64 == null) {
            JOptionPane.showMessageDialog(mainFrame, "Input email key.");
            return false;
        }
        if (authenticated.getUserPubKey().length == 0) {
            JOptionPane.showMessageDialog(mainFrame, "Generate new keys.");
            return false;
        }
        toServer.write(ClientCommands.SEND_FILENAME.getValue());
        toServer.write(filename.getBytes().length);
        toServer.write(filename.getBytes());
        return true;
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

    public void sendCurrentRSAKeyAndReceiveSessionKey() {
        if (socket == null) {
            JOptionPane.showMessageDialog(mainFrame, "Server is not available.");
            return;
        }
        if (authenticated.getUserPubKey() == null || authenticated.getUserPrKey() == null) {
            JOptionPane.showMessageDialog(mainFrame, "Generate new keys.");
            return;
        }

        byte[] publicKey = authenticated.getUserPubKey();

        byte[] receivedSessionKey = new byte[0];
        try {
            sendCurrentPublicRSAKey(publicKey);
            mainFrame.getLogTextArea().append("Sending to server current public RSA KEY\n");
            receivedSessionKey = receiveSessionEncodedKey();
            mainFrame.getLogTextArea().append("Receiving from server encoded session key\n");
        } catch (IOException e) {
            mainFrame.getLogTextArea().append(e.getMessage() + "\n");
            return;
        }
        DAOuser daOuser = new DAOuserImpl();
        if (receivedSessionKey == null) {
            JOptionPane.showConfirmDialog(mainFrame, "Session key wasn't received.");
            return;
        }
        authenticated.setUserSessionKey(receivedSessionKey);

        try {
            mainFrame.getLogTextArea().append("Saving keys to database.\n");
            daOuser.setSessionKey(authenticated);
        } catch (SQLException e) {
            JOptionPane.showConfirmDialog(mainFrame, e.getMessage());
            return;
        }
    }

    public void sendCredentials(String login, byte[] password) throws IOException {
        toServer.write(ClientCommands.SEND_CREDENTIALS.getValue());
        byte[] loginBytes = login.getBytes();
        int byteArrayLength = 8;

        //cause byte length may be more than 256
        byte[] loginLength = ByteBuffer.allocate(byteArrayLength).putInt(loginBytes.length).array();
        byte[] passwordLength = ByteBuffer.allocate(byteArrayLength).putInt(password.length).array();
        toServer.write(byteArrayLength);
        toServer.write(loginLength);
        toServer.write(loginBytes);
        toServer.write(passwordLength);
        toServer.write(password);
    }

    public byte[] receiveSessionToken() throws IOException {
        int byteLength = fromServer.read();
        byte[] sessionKeyByteLength = new byte[byteLength];
        fromServer.read(sessionKeyByteLength, 0, byteLength);
        int sessionTokenLength = ByteBuffer.wrap(sessionKeyByteLength).getInt();
        byte[] token = new byte[sessionTokenLength];
        fromServer.read(token, 0, sessionTokenLength);
        return token;
    }

    public ServerCommands getCredentialResult() throws IOException {
        int result = fromServer.read();
        return ServerCommands.getCommandByValue(result);
    }

    private void sendCurrentPublicRSAKey(byte[] publicKey) throws IOException {
        toServer.write(ClientCommands.SEND_CURRENT_PUBLIC_RSA_KEY.getValue());
        toServer.write(publicKey);
    }

    private String receiveAndDecodeFile(String alias, String password) throws IOException, SQLException, NoSuchAlgorithmException {
        mainFrame.getLogTextArea().append("Encoding file from server.\n");
        StringBuffer buffer = new StringBuffer();
        AES aes = new AES();
        aes.initCipher(CryptoSystem.AES);
        byte[] emailKey = new byte[0];
        try {
            emailKey = Base64.decodeFromBase64(emailKeyB64);
        } catch (IllegalArgumentException e) {
            toServer.write(ClientCommands.ERROR_FILE_RECEIVING.getValue());
            JOptionPane.showMessageDialog(mainFrame, "Invalid mail key");
            return null;
        }

        PrivateKey privateKey = null;
        try {
            privateKey = KeyStoreUtils.readEntry(alias, password);
        } catch (KeyStoreException e) {
            toServer.write(ClientCommands.ERROR_FILE_RECEIVING.getValue());
            JOptionPane.showMessageDialog(mainFrame, e.getMessage());
            return null;
        } catch (CertificateException e) {
            toServer.write(ClientCommands.ERROR_FILE_RECEIVING.getValue());
            JOptionPane.showMessageDialog(mainFrame, e.getMessage());
            return null;
        } catch (UnrecoverableEntryException e) {
            toServer.write(ClientCommands.ERROR_FILE_RECEIVING.getValue());
            JOptionPane.showMessageDialog(mainFrame, e.getMessage());
            return null;
        }

        toServer.write(ClientCommands.CORRECT_FILE_RECEIVING.getValue());
        RSA rsa = new RSA();
        rsa.initCipher(CryptoSystem.RSA);
        byte[] sessionKey = rsa.decode(authenticated.getUserSessionKey(), privateKey);

        //AES aes1 = new AES();
        aes.initCipher(CryptoSystem.AES_CBC);
        while (true) {
            int size = fromServer.read();
            if (size == 0) {
                int eof = fromServer.read();
                if (eof == ServerCommands.END_OF_FILE.getValue()) {
                    return buffer.toString();
                } else {
                    return null;
                }
            }
            byte[] encPartFile = new byte[size];
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            fromServer.read(encPartFile, 0, size);

            String partFileString = new String(aes.decode(encPartFile,
                    new SecretKeySpec(sessionKey, CryptoSystem.AES), ivspec));
            buffer.append(partFileString);
        }
    }

    private boolean isAuthenticated() {
        return authenticated != null;
    }
}
