package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by USER on 25.09.2016.
 */
public interface ServerAPI {
    void receivePublicRSAKey() throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException;
    void sendEncodedSessionKey() throws IOException;
    void sendEncodedFile(String filename) throws IOException;
    String receiveFilename() throws IOException;
}