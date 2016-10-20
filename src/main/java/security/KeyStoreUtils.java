package security;

import java.io.*;
import java.security.*;
import java.security.cert.*;

/**
 * Created by USER on 19.10.2016.
 */
public class KeyStoreUtils {
    private String keyStore = "KeyStoreRSA.jks";
    private static ClassLoader loader = ClassLoader.getSystemClassLoader();

    private static File file = new File(loader.getResource("KeyStoreRSA.jks").getFile());

    public static void writeEntry(String alias, PrivateKey key, String password)
            throws KeyStoreException,
            CertificateException,
            UnrecoverableEntryException {
        KeyStore ks = loadKs();
        KeyStore.PrivateKeyEntry prEntry =
                new KeyStore.PrivateKeyEntry(key, new java.security.cert.Certificate[] {ks.getCertificate("alias")});
        ks.setEntry(alias, prEntry, new KeyStore.PasswordProtection(password.toCharArray()));
        saveKs(ks);
    }

    public static PrivateKey readEntry(String alias, String password)
            throws KeyStoreException,
            CertificateException,
            NoSuchAlgorithmException,
            UnrecoverableEntryException {
        KeyStore ks = loadKs();
        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry)
                    ks.getEntry(alias, new KeyStore.PasswordProtection(password.toCharArray()));
        return pkEntry.getPrivateKey();
    }

    private static KeyStore loadKs() throws KeyStoreException, CertificateException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            ks.load(fis, "password".toCharArray());
        } catch (IOException ignored) {
        } catch (NoSuchAlgorithmException ignored) {
        } finally {
            try {
                fis.close();
            } catch (IOException ignored) {}
        }
        return ks;
    }

    private static void saveKs(KeyStore ks) throws CertificateException, KeyStoreException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            ks.store(fos, "password".toCharArray());
        } catch (FileNotFoundException ignored) {
        } catch (IOException ignored) {
        } catch (NoSuchAlgorithmException ignored) {

        } finally {
            try {
                fos.close();
            } catch (IOException ignored) {
            }
        }
    }
}
