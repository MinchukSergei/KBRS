package client;

import util.ServerCommands;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

/**
 * Created by USER on 23.09.2016.
 */
public interface ClientAPI {
    void sendKeyAndReceiveSessionKey(String keyStorePass);
    boolean sendFilename(String filename) throws IOException;
    String receiveFile(String password) throws IOException, NoSuchAlgorithmException, SQLException, InvalidKeySpecException;
    void sendCurrentRSAKeyAndReceiveSessionKey();
    void sendCredentials(String login, byte[] password) throws IOException;
    byte[] receiveSessionToken() throws IOException;
    ServerCommands getCredentialResult() throws IOException;
}
