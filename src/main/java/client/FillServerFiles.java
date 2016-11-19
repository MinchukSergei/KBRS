package client;

import dao.DAOFile;
import dao.impl.DAOFileImpl;
import security.AES;
import security.CryptoSystem;
import security.KeyStoreServerFileUtils;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.sql.SQLException;

/**
 * Created by USER on 18.11.2016.
 */
public class FillServerFiles {
    public static void main(String[] args) {
        String[] filenames = {"1.txt", "2.txt", "3.txt"};
        DAOFile daoFile = new DAOFileImpl();

        for (String s : filenames) {
            Path currentRelativePath = Paths.get("");
            String absolutePath = currentRelativePath.toAbsolutePath().toString();
            String fullFileName = absolutePath + "\\testFiles\\" + s;
            Path path = Paths.get(fullFileName);
            AES aes = new AES();
            aes.initCipher(CryptoSystem.AES);
            try {
                SecretKey sc = aes.generateKey();
                KeyStoreServerFileUtils.writeEntry(s, sc);
                byte[] file = Files.readAllBytes(path);
                byte[] encFile = aes.encode(file, sc);
                daoFile.setFile(encFile, s, true);
                byte[] readFile = daoFile.getFileByName(s, true);
                SecretKey scR = KeyStoreServerFileUtils.readEntry(s);
                String decFile = new String(aes.decode(readFile, scR));
                System.out.println(decFile);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (UnrecoverableEntryException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

        }
    }
}
