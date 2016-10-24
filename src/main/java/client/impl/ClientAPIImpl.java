package client.impl;

import client.ClientAPI;
import client.gui.ClientGui;
import dao.DAOuser;
import dao.impl.DAOuserImpl;
import entities.User;
import security.AES;
import security.CryptoSystem;
import security.KeyStoreUtils;
import security.RSA;
import util.ClientCommands;
import util.CredentialMessage;
import util.ServerCommands;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.text.BadLocationException;
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
    private int attemptCount = 3;
    private Socket socket;
    private User authenticated;
    private byte[] sessionKey;
    private byte[] sessionToken;

    private InputStream fromServer;
    private OutputStream toServer;

    private ClientGui mainFrame;

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

    public void sendKeyAndReceiveSessionKey(String keyStorePass) {
        if (socket == null) {
            JOptionPane.showMessageDialog(mainFrame, "Server is not available.");
            return;
        }
        KeyPair keyPair = genKeyPair();
        PublicKey publicKey = keyPair.getPublic();



        try {
            sendRSAKey(publicKey);
            mainFrame.getLogTextArea().append("Sending to server public RSA KEY\n");
            sessionKey = receiveSessionEncodedKey();
            mainFrame.getLogTextArea().append("Receiving from server encoded session key\n");
        } catch (IOException e) {
            mainFrame.getLogTextArea().append(e.getMessage() + "\n");
            return;
        }
        DAOuser daOuser = new DAOuserImpl();
        if (sessionKey == null) {
            JOptionPane.showConfirmDialog(mainFrame, "Session key wasn't received.");
            return;
        }
        authenticated.setUserPubKey(publicKey.getEncoded());

        PrivateKey privateKey = keyPair.getPrivate();
        try {
            KeyStoreUtils.writeEntry(authenticated.getUserLogin(), privateKey, keyStorePass);
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
        if (authenticated.getUserPubKey() == null) {
            JOptionPane.showMessageDialog(mainFrame, "Generate new keys.");
            return;
        }

        byte[] publicKey = authenticated.getUserPubKey();


        try {
            sendCurrentPublicRSAKey(publicKey);
            mainFrame.getLogTextArea().append("Sending to server current public RSA KEY\n");
            sessionKey = receiveSessionEncodedKey();
            mainFrame.getLogTextArea().append("Receiving from server encoded session key\n");
        } catch (IOException e) {
            mainFrame.getLogTextArea().append(e.getMessage() + "\n");
            return;
        }
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
        toServer.write(ClientCommands.GIVE_TOKEN.getValue());
        byte[] token = readFromServer();
        return token;
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

    private void sendCurrentPublicRSAKey(byte[] publicKey) throws IOException {
        toServer.write(ClientCommands.SEND_CURRENT_PUBLIC_RSA_KEY.getValue());
        toServer.write(publicKey);
    }

    private String receiveAndDecodeFile(String alias, String password) throws IOException, SQLException, NoSuchAlgorithmException {
        mainFrame.getLogTextArea().append("Encoding file from server.\n");
        StringBuffer buffer = new StringBuffer();
        AES aes = new AES();
        aes.initCipher(CryptoSystem.AES);

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
            JOptionPane.showMessageDialog(mainFrame, "Your pin is incorrect.");
            attemptCount -= 1;
            if (attemptCount <= 0) {
                mainFrame.getLogTextArea().append("Waiting...\n");
                mainFrame.setEnabled(false);
                new Thread() {
                    @Override
                    public void run() {
                        long time = 0;
                        long maxTime = (long) (30 * Math.pow(2, Math.abs(attemptCount)));
                        while (true) {
                            time += 1;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e1) {
                            }
                            mainFrame.getLogTextArea().append(maxTime - time + "\n");
                            if (time == maxTime) {
                                mainFrame.setEnabled(true);
                                break;
                            }
                        }
                    }
                }.start();
            }
            return null;
        }
        attemptCount = 3;

        toServer.write(ClientCommands.CORRECT_FILE_RECEIVING.getValue());
        RSA rsa = new RSA();
        rsa.initCipher(CryptoSystem.RSA);
        byte[] decodedSessionKey = rsa.decode(sessionKey, privateKey);

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
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            fromServer.read(encPartFile, 0, size);

            String partFileString = new String(aes.decode(encPartFile,
                    new SecretKeySpec(decodedSessionKey, CryptoSystem.AES), ivspec));
            buffer.append(partFileString);
        }
    }
}
