package database;

import util.ResourceBundleManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MySQLconnector {
    static final String DB_URL = "jdbc:mysql://localhost/kbsr";

    public void registerJDBCdriver() {
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL,
                    ResourceBundleManager.getByName("user.login"),
                    ResourceBundleManager.getByName("user.password"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

}
