package dao;

import entities.User;

import java.sql.SQLException;

public interface DAOuser {
    void registerUser(User user) throws SQLException;
    void setPrKey(User user) throws SQLException;
    void setSessionKey(User user) throws SQLException;
    User isConfirmed(String login, String pass) throws SQLException;
    String getPrKey(User user) throws SQLException;
}
