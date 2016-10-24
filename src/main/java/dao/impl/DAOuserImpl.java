package dao.impl;

import dao.DAOuser;
import database.MySQLconnector;
import entities.User;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by USER on 22.09.2016.
 */
public class DAOuserImpl implements DAOuser {
    private static final String REGISTER = "INSERT INTO registration_table" +
            " (user_login, user_password) VALUES (?, ?)";
    private static final String SET_PUB_KEY = "UPDATE registration_table" +
            " SET user_pub_key = ? WHERE user_login = ?";
    private static final String SET_DS_PUB_KEY = "UPDATE registration_table" +
            " SET user_ds_pub_key = ? WHERE user_login = ?";

    private static final String GET_KEY = "SELECT * FROM registration_table" +
            " WHERE user_login = ?";
    private static final String GET_USER = "SELECT * FROM registration_table" +
            " WHERE user_login = ? AND user_password = ?";

    MySQLconnector mySQLconnector;

    public DAOuserImpl() {
        mySQLconnector = new MySQLconnector();
        mySQLconnector.registerJDBCdriver();
    }

    public void registerUser(User user) throws SQLException {
        Connection connection = mySQLconnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(REGISTER);
        statement.setString(1, user.getUserLogin());
        statement.setString(2, new String(user.getUserPassword()));
        statement.execute();
        connection.close();
    }


    public void setPubKey(User user) throws SQLException {
        Connection connection = mySQLconnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(SET_PUB_KEY);
        statement.setBytes(1, user.getUserPubKey());
        statement.setString(2, user.getUserLogin());
        statement.execute();
        connection.close();
    }

    public void setDSPubKey(User user) throws SQLException {
        Connection connection = mySQLconnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(SET_DS_PUB_KEY);
        statement.setBytes(1, user.getUserDSPubKey());
        statement.setString(2, user.getUserLogin());
        statement.execute();
        connection.close();
    }

    public User isConfirmed(User user) throws SQLException {
        Connection connection = mySQLconnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_USER);
        statement.setString(1, user.getUserLogin());
        try {
            statement.setString(2, new String(user.getUserPassword(), "UTF-8"));
        } catch (UnsupportedEncodingException ignored) {}
        ResultSet rs = statement.executeQuery();

        if (rs.next()) {
            user = new User();
            user.setUserLogin(rs.getString("user_login"));
            user.setUserDSPubKey(rs.getBytes("user_ds_pub_key"));
            user.setUserPubKey(rs.getBytes("user_pub_key"));
        } else {
            user = null;
        }
        rs.close();
        connection.close();
        return user;
    }

    public void getKeys(User user) throws SQLException {
        Connection connection = mySQLconnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_KEY);
        statement.setString(1, user.getUserLogin());
        ResultSet rs = statement.executeQuery();

        if (rs.next()) {
            user.setUserDSPubKey(rs.getBytes("user_ds_pub_key"));
            user.setUserPubKey(rs.getBytes("user_pub_key"));
        }
        rs.close();
        connection.close();
    }
}
