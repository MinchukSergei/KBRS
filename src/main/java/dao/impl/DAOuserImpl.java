package dao.impl;

import dao.DAOuser;
import database.MySQLconnector;
import entities.User;
import security.MD5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by USER on 22.09.2016.
 */
public class DAOuserImpl implements DAOuser {
    private static final String REGISTER = "INSERT INTO registration_table" +
            " (user_login, user_password, user_email) VALUES (?, ?, ?)";
    private static final String SET_PR_KEY = "UPDATE registration_table" +
            " SET user_pr_key = ? WHERE user_login = ?";
    private static final String SET_SESSION_KEY = "UPDATE registration_table" +
            " SET user_session_key = ? WHERE user_login = ?";
    private static final String SET_PUB_KEY = "UPDATE registration_table" +
            " SET user_pub_key = ? WHERE user_login = ?";
    private static final String GET_KEY = "SELECT * FROM registration_table" +
            " WHERE user_login = ?";
    private static final String GET_USER = "SELECT * FROM registration_table" +
            " WHERE user_login = ? AND user_password = ?";
    private static final String UPDATE_EMAIL = "UPDATE registration_table" +
            " SET user_email = ? WHERE user_login = ?";

    MySQLconnector mySQLconnector;

    public DAOuserImpl() {
        mySQLconnector = new MySQLconnector();
        mySQLconnector.registerJDBCdriver();
    }

    public void registerUser(User user) throws SQLException {
        Connection connection = mySQLconnector.getConnection();
        String hashedPass = MD5.hashMd5(user.getUserPassword());

        PreparedStatement statement = connection.prepareStatement(REGISTER);
        statement.setString(1, user.getUserLogin());
        statement.setString(2, hashedPass);
        statement.setString(3, user.getUserEmail());
        statement.execute();
        connection.close();
    }

    public void setPrKey(User user) throws SQLException {
        Connection connection = mySQLconnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(SET_PR_KEY);
        statement.setBytes(1, user.getUserPrKey());
        statement.setString(2, user.getUserLogin());
        statement.execute();
        connection.close();
    }

    public void setSessionKey(User user) throws SQLException {
        Connection connection = mySQLconnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(SET_SESSION_KEY);
        statement.setBytes(1, user.getUserSessionKey());
        statement.setString(2, user.getUserLogin());
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

    public User isConfirmed(User user) throws SQLException {
        Connection connection = mySQLconnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_USER);
        String pass = user.getUserPassword();
        pass = MD5.hashMd5(pass);
        statement.setString(1, user.getUserLogin());
        statement.setString(2, pass);
        ResultSet rs = statement.executeQuery();

        if (rs.next()) {
            user = new User();
            user.setUserLogin(rs.getString("user_login"));
            user.setUserEmail(rs.getString("user_email"));
            user.setUserPrKey(rs.getBytes("user_pr_key"));
            user.setUserSessionKey(rs.getBytes("user_session_key"));
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
            user.setUserPrKey(rs.getBytes("user_pr_key"));
            user.setUserSessionKey(rs.getBytes("user_session_key"));
            user.setUserPubKey(rs.getBytes("user_pub_key"));
        }
        rs.close();
        connection.close();
    }

    public void updateEmail(User user) throws SQLException {
        Connection connection = mySQLconnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(UPDATE_EMAIL);
        statement.setString(1, user.getUserEmail());
        statement.setString(2, user.getUserLogin());
        statement.execute();
        connection.close();
    }
}
