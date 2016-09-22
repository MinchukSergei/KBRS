package dao;

import entities.User;

/**
 * Created by USER on 22.09.2016.
 */
public interface DAOuser {
    void registerUser(User user);
    void setKey(User user);
    boolean isRegistered(String login);
    boolean isConfirmed(String password);
}
