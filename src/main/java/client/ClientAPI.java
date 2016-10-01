package client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

/**
 * Created by USER on 23.09.2016.
 */
public interface ClientAPI {
    void sendKeyAndReceiveSessionKey();
    boolean sendFilename(String filename) throws IOException;
    String receiveFile() throws IOException, NoSuchAlgorithmException, SQLException, InvalidKeySpecException;
    void sendCurrentRSAKeyAndReceiveSessionKey();
}
