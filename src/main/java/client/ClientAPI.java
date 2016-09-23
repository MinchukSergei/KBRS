package client;

import entities.User;

/**
 * Created by USER on 23.09.2016.
 */
public interface ClientAPI {
    void sendKeyAndReceiveSessionKey();
    void registerNewUser(User user);
    void authenticateUser(String login, String pass);
    void useKeyFromMail(String key);
    void sendFilename(String filename);
}
