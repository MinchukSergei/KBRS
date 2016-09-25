package security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;


public abstract class CryptoSystem {
    private Cipher cipher;

    //Crypto system types
    public static final String AES = "AES";
    public static final String RSA = "RSA";
    //

    public void initCipher(String cryptoSystem) {
        try {
            cipher = Cipher.getInstance(cryptoSystem);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        } catch (NoSuchPaddingException e) {
            System.out.println(e.getMessage());
        }
    }

    public byte[] encode(byte[] toEncode, Key key) {
        byte[] encodedMessage = new byte[0];
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encodedMessage = cipher.doFinal(toEncode);
        } catch (InvalidKeyException e) {
            System.out.println(e.getMessage());
        } catch (BadPaddingException e) {
            System.out.println(e.getMessage());
        } catch (IllegalBlockSizeException e) {
            System.out.println(e.getMessage());
        }
        return encodedMessage;
    }

    public byte[] decode(byte[] toDecode, Key key) {
        byte[] decodedMsg = new byte[0];
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            decodedMsg = cipher.doFinal(toDecode);
        } catch (InvalidKeyException e) {
            System.out.println(e.getMessage());
        } catch (BadPaddingException e) {
            System.out.println(e.getMessage());
        } catch (IllegalBlockSizeException e) {
            System.out.println(e.getMessage());
        }
        return decodedMsg;
    }
}
