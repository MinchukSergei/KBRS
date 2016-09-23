package client;

import dao.DAOuser;
import dao.impl.DAOuserImpl;
import entities.User;
import mail.MailSender;
import security.AES;
import security.Base64;
import security.CryptoSystem;
import security.RSA;

import javax.crypto.SecretKey;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.util.Properties;

public class TestClient {
    public static void main(String[] args) throws InvalidKeyException {
        RSA rsa = new RSA();
        rsa.initCipher(CryptoSystem.RSA);
        KeyPair kp = rsa.generateKeyPair();
        AES aes = new AES();
        aes.initCipher(CryptoSystem.AES);
        SecretKey key = aes.generateKey();
        String b64key = Base64.encodeToBase64(key.getEncoded());

        byte[] encodedKey = rsa.encode(b64key, kp.getPublic());
        int y = encodedKey.length;

        DAOuser daOuser = new DAOuserImpl();
        User user;
//        user.setUserLogin("pes");
//        user.setUserPassword("gau");



//        AES aes = new AES();
//        aes.initCipher(AES.AES);
//        SecretKey sessKey = aes.generateKey();
//        String b64encodedKey = security.Base64.encodeToBase64(sessKey.getEncoded());
//        SecretKey key = aes.generateKey();
//        byte[] encKey = aes.encode(b64encodedKey, key);
//        String encodedKey = security.Base64.encodeToBase64(encKey);
//        int y = encodedKey.length();
//        user.setUserKey(encodedKey);
//        try {
//            daOuser.setPrKey(user);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }



    }
}
