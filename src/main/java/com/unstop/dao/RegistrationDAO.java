package com.unstop.dao;

import com.unstop.model.Registration;
import com.unstop.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RegistrationDAO - UPDATED from v1.
 * getByUser() now JOINs competition_type so dashboard can show it.
 * All other methods identical to v1.
 */
public class RegistrationDAO {

    public boolean register(int userId, int competitionId) {
        String sql = "INSERT IGNORE INTO registrations (user_id, competition_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, competitionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean isRegistered(int userId, int competitionId) {
        String sql = "SELECT id FROM registrations WHERE user_id=? AND competition_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, competitionId);
            return ps.executeQuery().next();
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** UPDATED: now also fetches competition_type for the dashboard type column */
    public List<Registration> getByUser(int userId) {
        String sql =
            "SELECT r.*, c.title AS competition_title, c.status AS competition_status, " +
            "       c.competition_type, c.id AS comp_id " +
            "FROM registrations r " +
            "JOIN competitions c ON r.competition_id = c.id " +
            "WHERE r.user_id = ? ORDER BY r.registered_at DESC";

        List<Registration> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Registration reg = new Registration();
                reg.setId(rs.getInt("id"));
                reg.setUserId(userId);
                reg.setCompetitionId(rs.getInt("competition_id"));
                reg.setRegisteredAt(rs.getTimestamp("registered_at"));
                reg.setCompetitionTitle(rs.getString("competition_title"));
                reg.setCompetitionStatus(rs.getString("competition_status"));
                reg.setCompetitionType(rs.getString("competition_type"));  // ← NEW
                list.add(reg);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public int countByCompetition(int competitionId) {
        String sql = "SELECT COUNT(*) FROM registrations WHERE competition_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, competitionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}
