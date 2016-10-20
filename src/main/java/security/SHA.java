package security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by USER on 19.10.2016.
 */
public class SHA {
    public static byte[] hash(String message) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] passBytes = message.getBytes();
        return sha256.digest(passBytes);
    }
}
