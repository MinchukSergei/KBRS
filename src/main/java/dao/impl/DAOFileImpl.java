package dao.impl;

import dao.DAOFile;
import database.MySQLconnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 18.11.2016.
 */
public class DAOFileImpl implements DAOFile {
    MySQLconnector mySQLconnector;

    private final String GET_FILE_BY_NAME = "SELECT enc_file FROM client_file " +
            "WHERE filename = ? AND isServer = ?";
    private final String SET_FILE = "INSERT INTO client_file " +
            "(filename, enc_file, isServer) VALUES (?, ?, ?)";
    private final String GET_FILENAMES = "SELECT filename FROM client_file WHERE isServer = ?";

    private final String UPDATE_FILE = "UPDATE client_file SET enc_file = ? WHERE filename = ?";

    public DAOFileImpl() {
        mySQLconnector = new MySQLconnector();
        mySQLconnector.registerJDBCdriver();
    }

    public byte[] getFileByName(String filename, boolean isServer) throws SQLException {
        Connection connection = mySQLconnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_FILE_BY_NAME);
        statement.setString(1, filename);
        statement.setBoolean(2, isServer);
        ResultSet rs = statement.executeQuery();
        byte[] encFile = new byte[0];
        if (rs.first()) {
            encFile = rs.getBytes("enc_file");
        }
        rs.close();
        statement.close();
        connection.close();
        return encFile;
    }

    public void setFile(byte[] file, String filename, boolean isServer) throws SQLException {
        Connection connection = mySQLconnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(SET_FILE);
        statement.setString(1, filename);
        statement.setBytes(2, file);
        statement.setBoolean(3, isServer);
        try {
            statement.execute();
        } catch (SQLException e) {
            statement.close();
            statement = connection.prepareStatement(UPDATE_FILE);
            statement.setBytes(1, file);
            statement.setString(2, filename);
            statement.execute();
        }

        statement.close();
        connection.close();
    }

    public String[] getFilenames(boolean isServer) throws SQLException {
        Connection connection = mySQLconnector.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_FILENAMES);
        statement.setBoolean(1, isServer);
        List<String> filenames = new ArrayList<>();
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            filenames.add(rs.getString("filename"));
        }
        rs.close();
        statement.execute();
        statement.close();
        connection.close();
        return filenames.toArray(new String[filenames.size()]);
    }
}
