package test;

import dao.DAOuser;
import dao.impl.DAOuserImpl;
import entities.User;
import security.MD5;
import security.SHA;

import java.sql.SQLException;
import java.util.Random;

public class Main {
    public static void main(String[] args) {

//        byte[] b = SHA.hash("11111");
//        byte[] f = new byte[32];
//        new Random().nextBytes(f);
//
//        DAOuser daOuser = new DAOuserImpl();
//        User user = new User();
//        user.setUserLogin("pes");
//        user.setUserKSData(f);
//        try {
//            daOuser.setKSData(user);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        byte[] res = new byte[32];
//
//        for (int i = 0; i < b.length; i++) {
//            res[i] = (byte) (b[i] ^ f[i]);
//            int y = b[i] ^ f[i];
//            int u = 0;
//        }
//        String r = new String(res);

        //DCyBnUnQJ82eb/LUrFSKHPJQVry/O2Dxeeyi3PnTqCw=
        int y = 0;
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
