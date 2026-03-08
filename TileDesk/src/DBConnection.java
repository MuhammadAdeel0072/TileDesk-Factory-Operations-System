package src;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DBConnection {

    // Database connection parameters - MODIFY THESE AS NEEDED
    private static final String URL = "jdbc:mysql://localhost:3306/tile_factory_db";
    private static final String USER = "root";       // Use your database user
    private static final String PASSWORD = "007036072"; // Use your database password
    
    // Alternative: Root credentials (use only if tile_app user doesn't exist)
    // private static final String USER = "root";
    // private static final String PASSWORD = "";

    private static Connection connection = null;

    /**
     * Get database connection using Singleton pattern
     * @return Connection object
     */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Establish the connection
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                
                // Test the connection
                if (connection != null && !connection.isClosed()) {
                    System.out.println("✅ Database connected successfully to: " + URL);
                }
                
            } catch (ClassNotFoundException e) {
                System.err.println("❌ MySQL JDBC Driver not found.");
                showErrorMessage("MySQL JDBC Driver not found.\nPlease add mysql-connector-java.jar to your classpath.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("❌ Failed to connect to the database.");
                
                // Provide user-friendly error message
                String errorMsg = "Database Connection Failed!\n\n";
                errorMsg += "Error: " + e.getMessage() + "\n\n";
                errorMsg += "Please ensure:\n";
                errorMsg += "1. MySQL server is running\n";
                errorMsg += "2. Database 'tile_factory_db' exists\n";
                errorMsg += "3. Username/Password are correct\n";
                errorMsg += "4. User has necessary permissions\n";
                errorMsg += "\nURL: " + URL;
                
                showErrorMessage(errorMsg);
                e.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * Get connection with auto-reconnect capability
     */
    public static Connection getConnectionWithRetry() {
        Connection conn = getConnection();
        if (conn == null) {
            // Try to reconnect once
            closeConnection();
            conn = getConnection();
        }
        return conn;
    }

    /**
     * Close database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("📴 Database connection closed.");
                }
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    /**
     * Check if connection is valid and alive
     */
    public static boolean isConnectionValid() {
        if (connection == null) {
            return false;
        }
        
        try {
            // Try a simple query to check connection
            return connection.isValid(5); // 5 second timeout
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Test database connection
     */
    public static boolean testConnection() {
        Connection testConn = null;
        try {
            // Try to establish a new connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            testConn = DriverManager.getConnection(URL, USER, PASSWORD);
            
            if (testConn != null && !testConn.isClosed()) {
                System.out.println("✅ Database connection test successful!");
                return true;
            }
        } catch (Exception e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            return false;
        } finally {
            if (testConn != null) {
                try {
                    testConn.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
        }
        return false;
    }

    /**
     * Show error message in dialog (Swing compatible)
     */
    private static void showErrorMessage(String message) {
        try {
            // Use invokeLater to ensure it runs on EDT
            javax.swing.SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                    null,
                    message,
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
                );
            });
        } catch (Exception e) {
            // If Swing is not available, print to console
            System.err.println(message);
        }
    }

    /**
     * Show success message in dialog
     */
    public static void showSuccessMessage(String message) {
        try {
            javax.swing.SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                    null,
                    message,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
            });
        } catch (Exception e) {
            System.out.println("✅ " + message);
        }
    }

    /**
     * Get database URL
     */
    public static String getDatabaseURL() {
        return URL;
    }

    /**
     * Get database name
     */
    public static String getDatabaseName() {
        return URL.substring(URL.lastIndexOf("/") + 1);
    }

    /**
     * Get database user
     */
    public static String getDatabaseUser() {
        return USER;
    }

    /**
     * Main method for testing the connection
     */
    public static void main(String[] args) {
        System.out.println("=== Testing Database Connection ===");
        System.out.println("Database URL: " + getDatabaseURL());
        System.out.println("Database Name: " + getDatabaseName());
        System.out.println("Database User: " + getDatabaseUser());
        
        boolean isConnected = testConnection();
        
        if (isConnected) {
            System.out.println("✅ Database connection successful!");
            
            // Test the singleton connection
            Connection conn1 = getConnection();
            Connection conn2 = getConnection();
            
            if (conn1 == conn2) {
                System.out.println("✅ Singleton pattern working correctly");
            }
            
            System.out.println("✅ Connection valid: " + isConnectionValid());
            
            closeConnection();
            System.out.println("✅ Connection closed successfully");
            
            // Try to get connection again (should reconnect)
            Connection conn3 = getConnection();
            if (conn3 != null) {
                System.out.println("✅ Reconnection successful");
            }
            
            closeConnection();
            
        } else {
            System.out.println("❌ Database connection failed!");
            System.out.println("\nTroubleshooting steps:");
            System.out.println("1. Check if MySQL server is running");
            System.out.println("2. Verify database 'tile_factory_db' exists");
            System.out.println("3. Check username and password");
            System.out.println("4. Ensure user has proper permissions");
            System.out.println("5. Add mysql-connector-java.jar to classpath");
        }
    }
}