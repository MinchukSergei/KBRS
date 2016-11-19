package security;

import client.PreparePassword;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by USER on 19.11.2016.
 */
public class StoragePasswordManager {
    public static String getStoragePassword() throws FileNotFoundException {
        byte[] partFromUsb = readPartFromUSB();
        byte[] randPart = Base64.decodeFromBase64(PreparePassword.passPart);
        byte[] encPass = readEncodedPass();
        byte[] resKey = new byte[randPart.length];

        for (int i = 0; i < resKey.length; i++) {
            resKey[i] = (byte) (randPart[i] ^ partFromUsb[i]);
        }
        SecretKey sc = new SecretKeySpec(resKey, 0, resKey.length, CryptoSystem.AES);
        AES aes = new AES();
        aes.initCipher(CryptoSystem.AES);
        String decPass = new String(aes.decode(encPass, sc));
        return decPass;
    }

    private static byte[] readEncodedPass() throws FileNotFoundException {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        String f = loader.getResource("pr_key/pass").getPath();
        File file = new File(f);
        byte[] encpass;
        try {
            encpass = Files.readAllBytes(Paths.get(file.toString()));
        } catch (IOException e) {
            throw new FileNotFoundException("Cannot find file with encoded storeage pass");
        }
        return encpass;
    }

    private static byte[] readPartFromUSB() throws FileNotFoundException {
        File usb;
        byte[] part;
        try {
            usb = File.listRoots()[3];
        } catch (IndexOutOfBoundsException e) {
            throw new FileNotFoundException("Insert you flash with keys.");
        }
        try {
            part = Files.readAllBytes(Paths.get(usb.toString() + "pass"));
        } catch (IOException e) {
            throw new FileNotFoundException("Key to this file is absent. Download from server this file.");
        }
        return part;
    }
}
