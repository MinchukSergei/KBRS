package security;

import javax.crypto.*;
import java.security.*;

/**
 * Created by USER on 22.09.2016.
 */
public class AES extends CryptoSystem {


    public SecretKey generateKey() {
        SecretKey key = null;
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
            keyGenerator.init(128);
            key = keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return key;
    }
}
