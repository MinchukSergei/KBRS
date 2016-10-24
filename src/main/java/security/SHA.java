package security;

import util.CredentialMessage;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created by USER on 19.10.2016.
 */
public class SHA {
    public static byte[] hash(String message) {
        MessageDigest sha256 = null;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ignored) {}
        byte[] passBytes = message.getBytes();
        return sha256.digest(passBytes);
    }

    public static byte[] hash(byte[] message) {
        return hash(new String(message));
    }

    public static byte[] generateSessionToken(CredentialMessage credentialMessage) {
        byte[] hashedLogin = SHA.hash(credentialMessage.getLogin());
        byte[] hashedPassword = SHA.hash(credentialMessage.getPassword());
        byte[] hashedSalt = SHA.hash(new Date().toString());
        ByteBuffer bb = ByteBuffer.allocate(hashedLogin.length + hashedPassword.length + hashedSalt.length);
        bb.put(hashedLogin);
        bb.put(hashedPassword);
        bb.put(hashedSalt);
        byte[] token = SHA.hash(bb.array());
        return token;
    }
}
