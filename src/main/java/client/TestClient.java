package client;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;

import database.MySQLconnector;
import org.apache.commons.codec.binary.Base64;
import security.AES;
import security.CryptoSystem;

public class TestClient {
    public static void main(String[] args) throws InvalidKeyException {
        MySQLconnector mySQLconnector = new MySQLconnector();
        mySQLconnector.registerJDBCdriver();
        Connection connection = mySQLconnector.getConnection();
        int y = 0;
//        CryptoSystem a = new AES();
//
//        KeyGenerator keyGenerator = null;
//        try {
//            keyGenerator = KeyGenerator.getInstance("AES");
//            keyGenerator.init(128);
//            SecretKey secretKey = keyGenerator.generateKey();
//            String encodedKey = java.util.Base64.getEncoder().encodeToString(secretKey.getEncoded());
//            System.out.println(encodedKey);
//            byte[] decodedKey = java.util.Base64.getDecoder().decode(encodedKey);
//            String stringToBeChecked = "...";
//            boolean isBase64 = Base64.isBase64(encodedKey);
//// rebuild key using SecretKeySpec
//            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
//            System.out.println(secretKey);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }



    }
}
