package server.impl;

import security.AES;
import security.CryptoSystem;
import security.RSA;
import server.ServerAPI;
import util.ClientCommands;
import util.ServerCommands;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by USER on 25.09.2016.
 */
public class ServerAPIImpl implements ServerAPI {
    private PublicKey publicKey;
    private SecretKey sessionKey;
    private Socket socket;

    private InputStream fromClient;
    private OutputStream toClient;

    public InputStream getFromClient() {
        return fromClient;
    }

    public void setFromClient(InputStream fromClient) {
        this.fromClient = fromClient;
    }

    public OutputStream getToClient() {
        return toClient;
    }

    public void setToClient(OutputStream toClient) {
        this.toClient = toClient;
    }

    public SecretKey getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(SecretKey sessionKey) {
        this.sessionKey = sessionKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public ServerAPIImpl(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void receivePublicRSAKey() throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
        int publicRSAKeyBytesLength = ClientCommands.RSA_PUBLIC_KEY_BYTE_LENGTH.getValue();
        byte[] rsaPublicKeyBytes = new byte[publicRSAKeyBytesLength];
        fromClient.read(rsaPublicKeyBytes, 0, publicRSAKeyBytesLength);
        if (rsaPublicKeyBytes.length < publicRSAKeyBytesLength) {
            System.out.println("Can't receive RSA public key.");
        } else {
            publicKey = KeyFactory.getInstance(CryptoSystem.RSA).
                    generatePublic(new X509EncodedKeySpec(rsaPublicKeyBytes));
        }
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

    public void sendEncodedSessionKey() throws IOException {
        byte[] encodedSessionKey = encodeSessionKey();
        if (encodedSessionKey == null) {
            System.out.println("Public key hasn't received.");
            return;
        }
        toClient.write(encodedSessionKey);
    }

    public void sendEncodedFile(String filename) throws IOException {
        if (sessionKey == null) {
            toClient.write(ServerCommands.SESSION_KEY_IS_NULL.getValue());
            return;
        }
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
}
