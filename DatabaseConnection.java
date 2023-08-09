package atm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/atm_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//                connection.close();
            }
            catch (ClassNotFoundException e)
            {
                System.err.println("MySQL JDBC driver not found. Make sure you have added the MySQL Connector/J library to your project.");
            }
            catch (SQLException e) {
                System.err.println("Error connecting to the database: " + e.getMessage());
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing the database connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
