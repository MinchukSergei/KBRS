package dao;

import entities.User;

import java.sql.SQLException;

public interface DAOuser {
    void registerUser(User user) throws SQLException;
    void setPubKey(User user) throws SQLException;
    void setDSPubKey(User user) throws SQLException;
    User isConfirmed(User user) throws SQLException;
    void getKeys(User user) throws SQLException;
    void setKSData(User user) throws SQLException;
}
