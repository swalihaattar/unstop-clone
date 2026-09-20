package com.unstop.dao;

import com.unstop.model.User;
import com.unstop.util.DBConnection;

import java.sql.*;

/**
 * UserDAO - Data Access Object for the `users` table.
 *
 * WHY DAO PATTERN:
 *   - Separates SQL from business logic (Servlet stays clean)
 *   - If we switch from JDBC to Hibernate, only DAO changes
 *   - Easy to explain in viva: "this class owns all user-related DB queries"
 */
public class UserDAO {

    /**
     * INSERT a new user into the database.
     * Returns true if insert succeeded.
     */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password, role, college) VALUES (?, ?, ?, ?, ?)";

        // try-with-resources: automatically closes connection + statement
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());   // already hashed by Servlet
            ps.setString(4, user.getRole());
            ps.setString(5, user.getCollege());

            return ps.executeUpdate() > 0;  // executeUpdate returns rows affected

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * SELECT user by email (used for login).
     * Returns null if no user found.
     */
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapRow(rs);  // convert ResultSet row → User object
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * SELECT user by id.
     */
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return mapRow(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * CHECK if email already exists (used during registration validation).
     */
    public boolean emailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();  // true if any row found

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Private helper: maps a ResultSet row into a User object.
     * Keeps mapping logic in one place - all methods reuse this.
     */
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        u.setCollege(rs.getString("college"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }
}
