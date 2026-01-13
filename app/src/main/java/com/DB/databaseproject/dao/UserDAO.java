package com.DB.databaseproject.dao;

import com.DB.databaseproject.model.User;
import com.DB.databaseproject.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User table
 * Handles all database operations for users
 * Uses multiline text blocks for SQL queries (matching working DBTest syntax)
 */
public class UserDAO {

    /**
     * Insert a new user into the database
     */
    public int insert(User user) throws SQLException {
        String sql = """
            INSERT INTO "User"
            ("User_Name", "Password", "Phone_Number", "Email", "First_Name", "Middle_Name", "Last_Name", "Role", "Address")
            VALUES (?, ?, ?, ?, ?, ?, ?, ?::user_role, ?)
            RETURNING "User_ID"
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 UserDAO.insert() - Executing INSERT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 Executing SQL:");
        System.out.println(sql);
        System.out.println("📝 Parameters:");
        System.out.println("   [1] User_Name: '" + user.getUserName() + "'");
        System.out.println("   [2] Password: '" + user.getPassword() + "'");
        System.out.println("   [3] Phone_Number: '" + user.getPhoneNumber() + "'");
        System.out.println("   [4] Email: '" + user.getEmail() + "'");
        System.out.println("   [5] First_Name: '" + user.getFirstName() + "'");
        System.out.println("   [6] Middle_Name: '" + user.getMiddleName() + "'");
        System.out.println("   [7] Last_Name: '" + user.getLastName() + "'");
        System.out.println("   [8] Role: '" + user.getRole() + "' (cast to ::user_role)");
        System.out.println("   [9] Address: '" + user.getAddress() + "'");
        System.out.println("⚡ Executing query...");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getPhoneNumber());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getFirstName());
            pstmt.setString(6, user.getMiddleName());
            pstmt.setString(7, user.getLastName());
            pstmt.setString(8, user.getRole()); // Will be cast to user_role by SQL
            pstmt.setString(9, user.getAddress());
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                int userId = rs.getInt("User_ID");
                System.out.println("✅ SUCCESS! User created with ID: " + userId);
                System.out.println("═══════════════════════════════════════════════");
                return userId;
            } else {
                System.err.println("❌ No User_ID returned from INSERT");
                System.out.println("═══════════════════════════════════════════════");
                return -1;
            }
        } catch (SQLException e) {
            System.err.println("\n❌ ❌ ❌ EMPLOYEE ADD ERROR - STEP 1 FAILED ❌ ❌ ❌");
            System.err.println("   Failed to insert User record");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("   Error Code: " + e.getErrorCode());
            System.err.println("   Failed Role value: '" + user.getRole() + "'");
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Update an existing user (with password update)
     */
    public boolean update(User user) throws SQLException {
        return update(user, true);
    }
    
    /**
     * Update an existing user (with optional password update)
     * @param user User object with updated data
     * @param updatePassword If true, password will be updated; if false, password field is ignored
     */
    public boolean update(User user, boolean updatePassword) throws SQLException {
        String sql;
        
        if (updatePassword) {
            sql = """
                UPDATE "User"
                SET "User_Name" = ?, "Password" = ?, "Phone_Number" = ?, "Email" = ?,
                    "First_Name" = ?, "Middle_Name" = ?, "Last_Name" = ?, "Role" = ?::user_role, "Address" = ?
                WHERE "User_ID" = ?
                """;
        } else {
            sql = """
                UPDATE "User"
                SET "User_Name" = ?, "Phone_Number" = ?, "Email" = ?,
                    "First_Name" = ?, "Middle_Name" = ?, "Last_Name" = ?, "Role" = ?::user_role, "Address" = ?
                WHERE "User_ID" = ?
                """;
        }
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 UserDAO.update() - Executing UPDATE");
        System.out.println("   Update Password: " + updatePassword);
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 Executing SQL:");
        System.out.println(sql);
        System.out.println("📝 Parameters:");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            
            pstmt.setString(paramIndex++, user.getUserName());
            System.out.println("   [" + (paramIndex - 1) + "] User_Name: '" + user.getUserName() + "'");
            
            if (updatePassword) {
                pstmt.setString(paramIndex++, user.getPassword());
                System.out.println("   [" + (paramIndex - 1) + "] Password: '" + user.getPassword() + "'");
            }
            
            pstmt.setString(paramIndex++, user.getPhoneNumber());
            System.out.println("   [" + (paramIndex - 1) + "] Phone_Number: '" + user.getPhoneNumber() + "'");
            
            pstmt.setString(paramIndex++, user.getEmail());
            System.out.println("   [" + (paramIndex - 1) + "] Email: '" + user.getEmail() + "'");
            
            pstmt.setString(paramIndex++, user.getFirstName());
            System.out.println("   [" + (paramIndex - 1) + "] First_Name: '" + user.getFirstName() + "'");
            
            pstmt.setString(paramIndex++, user.getMiddleName());
            System.out.println("   [" + (paramIndex - 1) + "] Middle_Name: '" + user.getMiddleName() + "'");
            
            pstmt.setString(paramIndex++, user.getLastName());
            System.out.println("   [" + (paramIndex - 1) + "] Last_Name: '" + user.getLastName() + "'");
            
            pstmt.setString(paramIndex++, user.getRole());
            System.out.println("   [" + (paramIndex - 1) + "] Role: '" + user.getRole() + "' (cast to ::user_role)");
            
            pstmt.setString(paramIndex++, user.getAddress());
            System.out.println("   [" + (paramIndex - 1) + "] Address: '" + user.getAddress() + "'");
            
            pstmt.setInt(paramIndex++, user.getUserId());
            System.out.println("   [" + (paramIndex - 1) + "] User_ID (WHERE): " + user.getUserId());
            
            System.out.println("⚡ Executing query...");
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ SUCCESS! Rows affected: " + rowsAffected);
            System.out.println("═══════════════════════════════════════════════");
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("\n❌ ❌ ❌ UPDATE ERROR ❌ ❌ ❌");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("   Error Code: " + e.getErrorCode());
            System.err.println("   Failed Role value: '" + user.getRole() + "'");
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Delete a user by ID
     */
    public boolean delete(int userId) throws SQLException {
        String sql = """
            DELETE FROM "User"
            WHERE "User_ID" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 UserDAO.delete() - Executing DELETE");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 User_ID: " + userId);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("✅ Rows affected: " + rowsAffected);
            System.out.println("═══════════════════════════════════════════════");
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ DELETE ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Delete a user by ID (explicit method name for clarity)
     */
    public boolean deleteUserById(int userId) throws SQLException {
        return delete(userId);
    }

    /**
     * Get user by ID
     */
    public User getById(int userId) throws SQLException {
        String sql = """
            SELECT * FROM "User"
            WHERE "User_ID" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 UserDAO.getById() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 User_ID: " + userId);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = extractUserFromResultSet(rs);
                System.out.println("✅ User found: " + user.getUserName());
                System.out.println("═══════════════════════════════════════════════");
                return user;
            } else {
                System.out.println("❌ No user found with ID: " + userId);
                System.out.println("═══════════════════════════════════════════════");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("❌ SELECT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Get user by username
     */
    public User getByUsername(String username) throws SQLException {
        String sql = """
            SELECT * FROM "User"
            WHERE "User_Name" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 UserDAO.getByUsername() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 User_Name: " + username);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = extractUserFromResultSet(rs);
                System.out.println("✅ User found: " + user.getUserName());
                System.out.println("═══════════════════════════════════════════════");
                return user;
            } else {
                System.out.println("❌ No user found with username: " + username);
                System.out.println("═══════════════════════════════════════════════");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("❌ SELECT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Authenticate user (login) - CRITICAL METHOD
     */
    public User authenticate(String username, String password) throws SQLException {
        String sql = """
            SELECT * FROM "User"
            WHERE "User_Name" = ? AND "Password" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 UserDAO.authenticate() - AUTHENTICATION ATTEMPT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Parameters:");
        System.out.println("   User_Name: '" + username + "'");
        System.out.println("   Password: '" + password + "'");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            System.out.println("⚡ Executing authentication query...");
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = extractUserFromResultSet(rs);
                System.out.println("✅ ✅ ✅ AUTHENTICATION SUCCESSFUL! ✅ ✅ ✅");
                System.out.println("👤 User ID: " + user.getUserId());
                System.out.println("👤 Username: " + user.getUserName());
                System.out.println("🎭 Role: " + user.getRole());
                System.out.println("📧 Email: " + user.getEmail());
                System.out.println("═══════════════════════════════════════════════");
                return user;
            } else {
                System.out.println("❌ ❌ ❌ AUTHENTICATION FAILED! ❌ ❌ ❌");
                System.out.println("💡 No matching user found in database");
                System.out.println("   Either username or password is incorrect");
                System.out.println("═══════════════════════════════════════════════");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("❌ ❌ ❌ AUTHENTICATION ERROR! ❌ ❌ ❌");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("   Error Code: " + e.getErrorCode());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Get all users
     */
    public List<User> getAll() throws SQLException {
        String sql = """
            SELECT * FROM "User"
            ORDER BY "User_ID"
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 UserDAO.getAll() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
            
            System.out.println("✅ Retrieved " + users.size() + " users");
            System.out.println("═══════════════════════════════════════════════");
            return users;
        } catch (SQLException e) {
            System.err.println("❌ SELECT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Get users by role
     */
    public List<User> getByRole(String role) throws SQLException {
        String sql = """
            SELECT * FROM "User"
            WHERE "Role" = ?
            ORDER BY "User_ID"
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 UserDAO.getByRole() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 Role: " + role);
        
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, role);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
            
            System.out.println("✅ Retrieved " + users.size() + " users with role: " + role);
            System.out.println("═══════════════════════════════════════════════");
            return users;
        } catch (SQLException e) {
            System.err.println("❌ SELECT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Check if username exists
     */
    public boolean usernameExists(String username) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM "User"
            WHERE "User_Name" = ?
            """;
        
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("🔍 UserDAO.usernameExists() - Executing SELECT");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println("📋 SQL: " + sql);
        System.out.println("📝 User_Name: " + username);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                System.out.println("✅ Username exists: " + exists);
                System.out.println("═══════════════════════════════════════════════");
                return exists;
            }
            System.out.println("═══════════════════════════════════════════════");
            return false;
        } catch (SQLException e) {
            System.err.println("❌ SELECT ERROR:");
            System.err.println("   Message: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("═══════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Extract User object from ResultSet
     * Uses exact column names from database
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("User_ID"),
            rs.getString("User_Name"),
            rs.getString("Password"),
            rs.getString("Phone_Number"),
            rs.getString("Email"),
            rs.getString("First_Name"),
            rs.getString("Middle_Name"),
            rs.getString("Last_Name"),
            rs.getString("Role"),
            rs.getString("Address")
        );
    }
}
