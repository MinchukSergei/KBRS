package client;

import security.*;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by USER on 19.11.2016.
 */
public class PreparePassword {
    public static String passPart = "AxAaTDCMhFnTikGmmrt3lw==";

    public static void main(String[] args) throws IOException {
        String encpass = StoragePasswordManager.getStoragePassword();
        System.out.println(encpass);

        String pass = "KBRS";
        AES aes = new AES();
        aes.initCipher(CryptoSystem.AES);
        SecretKey sc = aes.generateKey();
        byte[] encPass = aes.encode(pass.getBytes(), sc);
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        String f = loader.getResource("pr_key/pass").getFile();
        try (FileOutputStream fos = new FileOutputStream(f)){
            fos.write(encPass);
        }

        byte[] rand = new byte[sc.getEncoded().length];
        new Random().nextBytes(rand);

        byte[] res = new byte[sc.getEncoded().length];
        byte[] enc = sc.getEncoded();

        for (int i = 0; i < res.length; i++) {
            res[i] = (byte) (enc[i] ^ rand[i]);
        }
        System.out.println(Base64.encodeToBase64(rand));

        File usb;
        try {
            usb = File.listRoots()[3];
        } catch (IndexOutOfBoundsException e) {
            throw new FileNotFoundException("Insert you flash.");
        }
        try (FileOutputStream fs = new FileOutputStream(usb.toString() + "pass")) {
            fs.write(res);
        }
    }
}
