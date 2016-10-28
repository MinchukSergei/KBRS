package test;

import dao.DAOuser;
import dao.impl.DAOuserImpl;
import entities.User;
import security.CryptoSystem;
import security.MD5;
import security.RSA;
import security.SHA;

import java.io.*;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.SQLException;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        byte[] g = SHA.hash("11111");
        String s = String.format("%064x", new BigInteger(1, g));
        int y = 0;
//        ClassLoader loader = ClassLoader.getSystemClassLoader();
//        File f = new File(loader.getResource("pr_key/pes_KBRS").getFile());
//        File f2 = new File(loader.getResource("pr_key/pes_KBRS2").getFile());
//
//        RSA rsa = new RSA();
//        rsa.initCipher(CryptoSystem.RSA);
//        KeyPair kp = rsa.generateKeyPair();
//        PublicKey pk = kp.getPublic();
//        PrivateKey prk = kp.getPrivate();
//
//
//        byte[] encPk = pk.getEncoded();
//        DAOuser daOuser = new DAOuserImpl();
//        User u = new User();
//        u.setUserLogin("pes");
//        u.setUserDSPubKey(encPk);
//        try {
//            daOuser.setDSPubKey(u);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        byte[] encPrk = prk.getEncoded();
//        byte[] rnd = new byte[encPrk.length];
//        new Random().nextBytes(rnd);
//
//        byte[] result = new byte[encPrk.length];
//
//        for (int i = 0; i < encPrk.length; i++) {
//            result[i] = (byte)(encPrk[i] ^ rnd[i]);
//        }
//
//
//
//        try {
//            FileOutputStream fis = new FileOutputStream(f);
//            FileOutputStream fis2 = new FileOutputStream(f2);
//            fis.write(result);
//            fis2.write(rnd);
//            fis.close();
//            fis2.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


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
