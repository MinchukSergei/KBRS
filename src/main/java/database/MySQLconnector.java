package database;

import util.ResourceBundleManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by USER on 22.09.2016.
 */
public class MySQLconnector {
    static final String DB_URL = "jdbc:mysql://localhost/kbsr";

    private Connection connection;

    public void registerJDBCdriver() {
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(DB_URL,
                        ResourceBundleManager.getByName("user.login"),
                        ResourceBundleManager.getByName("user.password"));
                return connection;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            return connection;
        }
        return connection;
    }


}
