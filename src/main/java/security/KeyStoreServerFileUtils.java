package security;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Created by USER on 19.11.2016.
 */
public class KeyStoreServerFileUtils {
    private static String keyStore = "KSServerFiles.jck";
    private static ClassLoader loader = ClassLoader.getSystemClassLoader();

    private static File file = new File(loader.getResource(keyStore).getFile());

    public static void writeEntry(String alias, SecretKey key)
            throws KeyStoreException,
            CertificateException,
            UnrecoverableEntryException {
        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(key);
        KeyStore ks = loadKs();
        String password = Base64.encodeToBase64(SHA.hash(alias));
        ks.setEntry(alias, secretKeyEntry, new KeyStore.PasswordProtection(password.toCharArray()));
        saveKs(ks);
    }

    public static SecretKey readEntry(String alias)
            throws KeyStoreException,
            CertificateException,
            NoSuchAlgorithmException,
            UnrecoverableEntryException {
        KeyStore ks = loadKs();
        String password = Base64.encodeToBase64(SHA.hash(alias));
        KeyStore.SecretKeyEntry pkEntry =
                (KeyStore.SecretKeyEntry) ks.getEntry(alias, new KeyStore.PasswordProtection(password.toCharArray()));
        return pkEntry.getSecretKey();
    }

    private static KeyStore loadKs() throws KeyStoreException, CertificateException {
        KeyStore ks = KeyStore.getInstance("JCEKS");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            ks.load(fis, "password".toCharArray());
        } catch (IOException ignored) {
            int h = 0;
        } catch (NoSuchAlgorithmException ignored) {
            int y = 0;
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
