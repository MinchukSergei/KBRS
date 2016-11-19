package dao;

import java.sql.SQLException;

/**
 * Created by USER on 18.11.2016.
 */
public interface DAOFile {
    byte[] getFileByName(String filename, boolean isServer) throws SQLException;
    void setFile(byte[] file, String filename, boolean isServer) throws SQLException;
    String[] getFilenames(boolean isServer) throws SQLException;
}
