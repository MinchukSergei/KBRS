package security;

import dao.DAOFile;
import dao.impl.DAOFileImpl;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.sql.SQLException;

/**
 * Created by USER on 18.11.2016.
 */
public class FileEncrypter {
    public void saveFileToClient(String filename, String file) throws SQLException, FileNotFoundException {
        AES aes = new AES();
        aes.initCipher(CryptoSystem.AES);
        SecretKey sk = aes.generateKey();
        saveKeyToFile(filename, sk.getEncoded());
        byte[] encFile = aes.encode(file.getBytes(), sk);
        DAOFile daoFile = new DAOFileImpl();
        daoFile.setFile(encFile, filename, false);
    }

    private void saveKeyToFile(String filename, byte[] aesKey) throws FileNotFoundException {
        File usb;
        try {
            usb = File.listRoots()[3];
        } catch (IndexOutOfBoundsException e) {
            throw new FileNotFoundException("Insert you flash with keys.");
        }
        try (FileOutputStream fs = new FileOutputStream(usb.toString() + Base64.encodeToBase64(SHA.hash(filename)))) {
            fs.write(aesKey);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    private byte[] getKeyFromFile(String filename) throws FileNotFoundException {
        File usb;
        byte[] key;
        try {
            usb = File.listRoots()[3];
        } catch (IndexOutOfBoundsException e) {
            throw new FileNotFoundException("Insert you flash with keys.");
        }
        try {
            key = Files.readAllBytes(Paths.get(usb.toString() + Base64.encodeToBase64(SHA.hash(filename))));
        } catch (IOException e) {
            throw new FileNotFoundException("Key to this file is absent. Download from server this file.");
        }
        return key;
    }

    public String getClientFile(String filename) throws FileNotFoundException, SQLException {
        byte[] keyBytes = getKeyFromFile(filename);
        SecretKey sc = new SecretKeySpec(keyBytes, 0, keyBytes.length, CryptoSystem.AES);
        AES aes = new AES();
        aes.initCipher(CryptoSystem.AES);
        DAOFile daoFile = new DAOFileImpl();
        byte[] encFile = daoFile.getFileByName(filename, false);
        String file = new String(aes.decode(encFile, sc));
        return file;
    }


}
