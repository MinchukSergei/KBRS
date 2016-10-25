package client;

import util.CredentialMessage;
import util.ServerCommands;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

/**
 * Created by USER on 23.09.2016.
 */
public interface ClientAPI {
    void sendKeyAndReceiveSessionKey();
    boolean sendFilename(String filename) throws IOException;
    String receiveFile(String password) throws IOException, NoSuchAlgorithmException, SQLException, InvalidKeySpecException;

    //void sendDSPublicKey(PublicKey publicKey) throws IOException;
    void sendCredentials(CredentialMessage credentialMessage) throws IOException;
    ServerCommands receiveCredentialResult() throws IOException;

    byte[] receiveSessionToken() throws IOException;

    void sendSessionToken(byte[] sessionToken) throws IOException;
}
