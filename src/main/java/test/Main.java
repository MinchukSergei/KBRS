package test;

import dao.DAOuser;
import dao.impl.DAOuserImpl;
import entities.User;
import security.*;
import util.CredentialMessage;

import java.io.FileNotFoundException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
//        try {
//            byte[] prKeyBytes = PrKeyReader.getPrKey("pes");
//            try {
//                PrivateKey pk = KeyFactory.getInstance(CryptoSystem.RSA).generatePrivate(new PKCS8EncodedKeySpec(prKeyBytes));
//                int u = 0;
//            } catch (InvalidKeySpecException e) {
//                e.printStackTrace();
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        RSA rsa = new RSA();
//        rsa.initCipher(CryptoSystem.RSA);
//        KeyPair kp = rsa.generateKeyPair();
//        PrivateKey pk = kp.getPrivate();
//        DAOuser daOuser = new DAOuserImpl();
//        User user = new User();
//        user.setUserLogin("pes");
//        user.setUserPassword(MD5.hashMd5("11111").getBytes());
//        try {
//            user = daOuser.isConfirmed(user);
//            int y = 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        CredentialMessage cm = DS.signCredentials("Vasya", "loh", pk);
//        boolean res = DS.checkSign(cm, kp.getPublic());
//        int u = 0;
    }
}
