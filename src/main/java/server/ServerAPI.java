package server;

import util.CredentialMessage;

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

    //void receiveDSPublicKey() throws IOException;

    CredentialMessage receiveCredentials() throws IOException;
    boolean sendCredentialsCheckResult(CredentialMessage credentialMessage) throws IOException;

    void sendSessionToken(byte[] sessionToken) throws IOException;


}
