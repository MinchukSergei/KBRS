package security;

import javax.crypto.*;
import java.security.*;
import java.util.Arrays;

public class RSA extends CryptoSystem {


    public KeyPair generateKeyPair() {
        KeyPair keyPair = null;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
            SecureRandom random = new SecureRandom();
            keyPairGenerator.initialize(2048, random);
            keyPair = keyPairGenerator.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyPair;
    }
}
