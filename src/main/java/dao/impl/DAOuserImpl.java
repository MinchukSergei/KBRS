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
    private static final String GET_KEY = "SELECT user_pr_key FROM registration_table" +
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
        statement.setString(1, user.getUserPrKey());
        statement.setString(2, user.getUserLogin());
        statement.execute();
        connection.close();
    }

    public void setSessionKey(User user) throws SQLException {
        Connection connection = mySQLconnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(SET_SESSION_KEY);
        statement.setString(1, user.getUserSessionKey());
        statement.setString(2, user.getUserLogin());
        statement.execute();
        connection.close();
    }

    public User isConfirmed(String login, String pass) throws SQLException {
        Connection connection = mySQLconnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_USER);
        pass = MD5.hashMd5(pass);
        statement.setString(1, login);
        statement.setString(2, pass);
        ResultSet rs = statement.executeQuery();
        User user = null;
        if (rs.next()) {
            user = new User();
            user.setUserLogin(rs.getString("user_login"));
            user.setUserEmail(rs.getString("user_email"));
            user.setUserPrKey(rs.getString("user_pr_key"));
            user.setUserSessionKey(rs.getString("user_session_key"));
        }
        rs.close();
        connection.close();
        return user;
    }

    public String getPrKey(User user) throws SQLException {
        Connection connection = mySQLconnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_KEY);
        statement.setString(1, user.getUserLogin());
        ResultSet rs = statement.executeQuery();

        String key = "";
        if (rs.next()) {
            key = rs.getString("user_pr_key");
        }
        rs.close();
        connection.close();
        return key;
    }
}
