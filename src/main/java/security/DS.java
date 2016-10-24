package security;

import util.CredentialMessage;

import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

/**
 * Created by USER on 24.10.2016.
 */
public class DS {
    private static RSA rsa;
    static {
        rsa = new RSA();
        rsa.initCipher(CryptoSystem.RSA);
    }

    public static CredentialMessage signCredentials(String login, String password, PrivateKey privateKey) {
        byte[] loginBytes = login.getBytes();
        byte[] hashedPassword = MD5.hashMd5(password).getBytes();
        ByteBuffer loginPass = ByteBuffer.allocate(loginBytes.length + hashedPassword.length);
        loginPass.put(loginBytes);
        loginPass.put(hashedPassword);

        byte[] sign = rsa.encode(loginPass.array(), privateKey);
        return new CredentialMessage(login, hashedPassword, sign);
    }

    public static boolean checkSign(CredentialMessage message, PublicKey key) {
        byte[] decodedSign = rsa.decode(message.getSign(), key);
        byte[] loginBytes = message.getLogin().getBytes();
        byte[] hashedPassword = message.getPassword();
        ByteBuffer loginPass = ByteBuffer.allocate(loginBytes.length + hashedPassword.length);
        loginPass.put(loginBytes);
        loginPass.put(hashedPassword);
        byte[] data = loginPass.array();
        return Arrays.equals(decodedSign, data);
    }
}
