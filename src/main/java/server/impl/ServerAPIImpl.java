package server.impl;

import dao.DAOuser;
import dao.impl.DAOuserImpl;
import entities.User;
import security.AES;
import security.CryptoSystem;
import security.DS;
import security.RSA;
import server.ServerAPI;
import util.ClientCommands;
import util.CredentialMessage;
import util.ServerCommands;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;

/**
 * Created by USER on 25.09.2016.
 */
public class ServerAPIImpl implements ServerAPI {
    private PublicKey publicKey;
    private SecretKey sessionKey;
    private Socket socket;

    private InputStream fromClient;
    private OutputStream toClient;

    public void setFromClient(InputStream fromClient) {
        this.fromClient = fromClient;
    }

    public void setToClient(OutputStream toClient) {
        this.toClient = toClient;
    }

    public ServerAPIImpl(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public void receivePublicRSAKey() throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] rsaPublicKeyBytes = readFromClient();
        publicKey = KeyFactory.getInstance(CryptoSystem.RSA).generatePublic(new X509EncodedKeySpec(rsaPublicKeyBytes));
    }

    private byte[] encodeSessionKey() {
        if (publicKey == null) {
            return null;
        }
        generateAndSetSessionKey();
        RSA rsa = new RSA();
        rsa.initCipher(CryptoSystem.RSA);
        return rsa.encode(sessionKey.getEncoded(), publicKey);
    }

    private void generateAndSetSessionKey() {
        AES aes = new AES();
        aes.initCipher(CryptoSystem.AES_CBC);
        sessionKey = aes.generateKey();
    }

    public void sendKSData(byte[] ksData) throws IOException {
        writeToClient(ksData);
    }

    public void sendEncodedSessionKey() throws IOException {
        byte[] encodedSessionKey = encodeSessionKey();
        if (encodedSessionKey == null) {
            toClient.write(ServerCommands.PUBLIC_KEY_IS_NULL.getValue());
            return;
        }
        toClient.write(ServerCommands.PUBLIC_KEY_IS_CORRECT.getValue());
        writeToClient(encodedSessionKey);
    }

    public void sendEncodedFile(String filename) throws IOException {
        Path currentRelativePath = Paths.get("");
        String absolutePath = currentRelativePath.toAbsolutePath().toString();
        String fullFileName = absolutePath + "\\testFiles\\" + filename;
        File file = new File(fullFileName);
        if (file.exists()) {
            toClient.write(ServerCommands.FILE_EXISTS.getValue());
            int error = fromClient.read();
            if (error == ClientCommands.ERROR_FILE_RECEIVING.getValue()) {
                return;
            }
            if (error == ClientCommands.CORRECT_FILE_RECEIVING.getValue()) {
                AES aes = new AES();
                aes.initCipher(CryptoSystem.AES_CBC);

                sendEncodedSessionKey();

                InputStream fileStream = new FileInputStream(file);
                byte[] partFile = new byte[ServerCommands.SERVER_PART_FILE_LENGTH.getValue()];

                byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
                IvParameterSpec ivspec = new IvParameterSpec(iv);
                while (fileStream.read(partFile) != -1) {
                    byte[] encPartFile = aes.encode(partFile, sessionKey, ivspec);
                    toClient.write(encPartFile.length);
                    toClient.write(encPartFile);
                }
                toClient.write(0);
                toClient.write(ServerCommands.END_OF_FILE.getValue());
                fileStream.close();
            }
        } else {
            toClient.write(ServerCommands.FILE_NOT_FOUND.getValue());
        }
    }

    public String receiveFilename() throws IOException {
        int lengthFilename = fromClient.read();
        byte[] filenameBytes = new byte[lengthFilename];
        int n = fromClient.read(filenameBytes, 0, lengthFilename);
        if (n < lengthFilename) {
            return null;
        } else {
            return new String(filenameBytes);
        }
    }

//    public void receiveDSPublicKey() throws IOException {
//        byte[] publicDSKeyBytes = readFromClient();
//        try {
//            publicDSKey = KeyFactory.getInstance(CryptoSystem.RSA).generatePublic(new X509EncodedKeySpec(publicDSKeyBytes));
//        } catch (InvalidKeySpecException ignored) {}
//        catch (NoSuchAlgorithmException ignored) {}
//    }

    public CredentialMessage receiveCredentials() throws IOException {
        byte[] loginBytes = readFromClient();
        byte[] hashedPasswordBytes = readFromClient();
        byte[] sign = readFromClient();
        return new CredentialMessage(new String(loginBytes), hashedPasswordBytes, sign);
    }

    public void sendSessionToken(byte[] sessionToken) throws IOException {
        writeToClient(sessionToken);
    }

    public boolean sendCredentialsCheckResult(CredentialMessage credentialMessage) throws IOException {
        DAOuser daOuser = new DAOuserImpl();
        User user = new User();
        user.setUserLogin(credentialMessage.getLogin());
        user.setUserPassword(credentialMessage.getPassword());
        try {
            user = daOuser.isConfirmed(user);
        } catch (SQLException e) {}
        if (user == null) {
            toClient.write(ServerCommands.INCORRECT_CREDENTIALS.getValue());
            return false;
        } else {
            toClient.write(ServerCommands.CORRECT_CREDENTIALS.getValue());
        }

        PublicKey publicDSKey = null;
        try {
            publicDSKey = KeyFactory.getInstance(CryptoSystem.RSA).generatePublic(new X509EncodedKeySpec(user.getUserDSPubKey()));
        } catch (InvalidKeySpecException e) {
            System.err.println(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
        }
        boolean checkSign = DS.checkSign(credentialMessage, publicDSKey);
        if (!checkSign) {
            toClient.write(ServerCommands.INCORRECT_SIGN.getValue());
            return false;
        } else {
            toClient.write(ServerCommands.CORRECT_SIGN.getValue());
        }

        return true;
    }

    public void sendResultCheckSessionToken(ServerCommands commands) throws IOException {
        toClient.write(commands.getValue());
    }

    public byte[] receiveSessionToken() throws IOException {
        return readFromClient();
    }

    private void writeToClient(byte[] msg) throws IOException {
        int byteArrayLength = 8;
        toClient.write(byteArrayLength);
        byte[] msgLength = ByteBuffer.allocate(byteArrayLength).putInt(msg.length).array();
        toClient.write(msgLength);
        toClient.write(msg);
    }

    private byte[] readFromClient() throws IOException {
        int byteArrayLength = fromClient.read();
        byte[] msgLengthBytes = new byte[byteArrayLength];
        fromClient.read(msgLengthBytes);
        int msgLength = ByteBuffer.wrap(msgLengthBytes).getInt();
        byte[] msg = new byte[msgLength];
        fromClient.read(msg);
        return msg;
    }
}
