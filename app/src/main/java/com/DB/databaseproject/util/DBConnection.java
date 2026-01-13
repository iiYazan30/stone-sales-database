package com.DB.databaseproject.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Utility
 * Manages PostgreSQL database connections
 * 
 * IMPORTANT: Creates a NEW connection for each request (no static caching)
 * This prevents connection issues with PostgreSQL + JavaFX
 */
public class DBConnection {
    
    // PostgreSQL connection details
    // Database: postgres, User: stones, Schema: public
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres?currentSchema=public";
    private static final String USER = "stones";
    private static final String PASSWORD = "123456";
    
    // Load JDBC driver once (static initializer)
    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ PostgreSQL JDBC Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ CRITICAL: PostgreSQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    /**
     * Private constructor to prevent instantiation
     */
    private DBConnection() {
    }

    /**
     * Get a NEW database connection
     * 
     * IMPORTANT: This method creates a FRESH connection every time!
     * No connection caching/reuse - this is intentional for PostgreSQL + JavaFX
     * 
     * @return NEW Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            System.out.println("🔌 Opening NEW database connection...");
            System.out.println("   Database: postgres");
            System.out.println("   User: " + USER);
            System.out.println("   Schema: public");
            
            // Create a FRESH connection (no caching!)
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Database connection established successfully!");
                System.out.println("   Connection valid: " + conn.isValid(2));
                System.out.println("   Catalog: " + conn.getCatalog());
                System.out.println("   Schema: " + conn.getSchema());
                return conn;
            } else {
                throw new SQLException("Connection is null or closed!");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to database!");
            System.err.println("   URL: " + URL);
            System.err.println("   User: " + USER);
            System.err.println("   Error: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            throw e;
        }
    }

    /**
     * Close a database connection
     * 
     * @param conn Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    System.out.println("✅ Database connection closed successfully!");
                }
            } catch (SQLException e) {
                System.err.println("❌ Error closing database connection!");
                e.printStackTrace();
            }
        }
    }

    /**
     * Test database connection
     * 
     * @return true if connection is successful
     */
    public static boolean testConnection() {
        Connection conn = null;
        try {
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║         DATABASE CONNECTION TEST                           ║");
            System.out.println("╚════════════════════════════════════════════════════════════╝");
            
            conn = getConnection();
            
            if (conn != null && !conn.isClosed() && conn.isValid(5)) {
                System.out.println("✅ Connection test PASSED!");
                System.out.println("✅ Can connect to database: " + conn.getCatalog());
                System.out.println("✅ Using schema: " + conn.getSchema());
                System.out.println("✅ Connection is valid!");
                
                // Test query to verify table access
                var stmt = conn.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM \"User\"");
                if (rs.next()) {
                    System.out.println("✅ Can access User table! Row count: " + rs.getInt(1));
                }
                rs.close();
                stmt.close();
                
                return true;
            } else {
                System.out.println("❌ Connection test FAILED - connection is invalid!");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection test FAILED!");
            System.err.println("   Error: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            e.printStackTrace();
            return false;
        } finally {
            closeConnection(conn);
        }
    }
}
