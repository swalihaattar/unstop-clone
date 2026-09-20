package com.unstop.dao;

import com.unstop.model.Team;
import com.unstop.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * TeamDAO - all DB operations for teams and team_members tables.
 *
 * Key design decisions:
 * - invite_code is a random 6-char alphanumeric string generated here
 * - Creating a team also auto-adds the leader as a member
 * - joinTeam() checks max team size from competitions table before inserting
 */
public class TeamDAO {

    /**
     * Create a new team. The leader is automatically added as first member.
     * Returns the new team id, or -1 on failure.
     */
    public int createTeam(String name, int competitionId, int leaderId) {
        String insertTeam = "INSERT INTO teams (name, competition_id, leader_id, invite_code) VALUES (?,?,?,?)";
        String addLeader = "INSERT INTO team_members (team_id, user_id) VALUES (?,?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // transaction: both inserts must succeed together

            try (PreparedStatement ps = conn.prepareStatement(insertTeam, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setInt(2, competitionId);
                ps.setInt(3, leaderId);
                ps.setString(4, generateCode());
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (!keys.next()) {
                    conn.rollback();
                    return -1;
                }
                int teamId = keys.getInt(1);

                // Add leader as member
                try (PreparedStatement ps2 = conn.prepareStatement(addLeader)) {
                    ps2.setInt(1, teamId);
                    ps2.setInt(2, leaderId);
                    ps2.executeUpdate();
                }

                conn.commit();
                return teamId;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Join an existing team by invite code.
     * Validates: code exists, user not already in a team for this competition,
     * team not full (based on competition.team_size_max).
     *
     * Returns: "ok" | "not_found" | "already_in_team" | "team_full"
     */
    public String joinTeam(String inviteCode, int userId) {
        String findTeam = "SELECT t.id, t.competition_id, c.team_size_max " +
                "FROM teams t JOIN competitions c ON t.competition_id = c.id " +
                "WHERE t.invite_code = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(findTeam)) {

            ps.setString(1, inviteCode.toUpperCase());
            ResultSet rs = ps.executeQuery();

            if (!rs.next())
                return "not_found";

            int teamId = rs.getInt("id");
            int compId = rs.getInt("competition_id");
            int maxSize = rs.getInt("team_size_max");

            if (hasTeamSubmitted(teamId, compId)) {
                return "locked";
            }

            // Check if user is already in any team for this competition
            if (userHasTeam(conn, userId, compId))
                return "already_in_team";

            // Check current member count
            int currentCount = getMemberCount(conn, teamId);
            if (currentCount >= maxSize)
                return "team_full";

            // All good — add member
            String insert = "INSERT IGNORE INTO team_members (team_id, user_id) VALUES (?,?)";
            try (PreparedStatement ps2 = conn.prepareStatement(insert)) {
                ps2.setInt(1, teamId);
                ps2.setInt(2, userId);
                ps2.executeUpdate();
            }
            return "ok";

        } catch (SQLException e) {
            e.printStackTrace();
            return "error";
        }
    }

    /**
     * Get the team a user belongs to for a specific competition. Returns null if
     * none.
     */
    public Team getTeamForUser(int userId, int competitionId) {
        String sql = "SELECT t.*, u.name AS leader_name, c.title AS comp_title, " +
                "(SELECT COUNT(*) FROM team_members tm WHERE tm.team_id = t.id) AS member_count " +
                "FROM teams t " +
                "JOIN team_members tm2 ON tm2.team_id = t.id AND tm2.user_id = ? " +
                "JOIN users u ON t.leader_id = u.id " +
                "JOIN competitions c ON t.competition_id = c.id " +
                "WHERE t.competition_id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, competitionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return mapRow(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all teams for a competition (for organizer view).
     */
    public List<Team> getTeamsByCompetition(int competitionId) {
        String sql = "SELECT t.*, u.name AS leader_name, c.title AS comp_title, " +
                "(SELECT COUNT(*) FROM team_members tm WHERE tm.team_id = t.id) AS member_count " +
                "FROM teams t " +
                "JOIN users u ON t.leader_id = u.id " +
                "JOIN competitions c ON t.competition_id = c.id " +
                "WHERE t.competition_id = ? ORDER BY t.created_at";
        List<Team> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, competitionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get member names for a team.
     */
    public List<String> getMemberNames(int teamId) {
        String sql = "SELECT u.name FROM team_members tm JOIN users u ON tm.user_id = u.id WHERE tm.team_id = ?";
        List<String> names = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                names.add(rs.getString("name"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return names;
    }

    // ── Private helpers ──────────────────────────────────────────

    private boolean userHasTeam(Connection conn, int userId, int competitionId) throws SQLException {
        String sql = "SELECT 1 FROM team_members tm JOIN teams t ON tm.team_id = t.id " +
                "WHERE tm.user_id = ? AND t.competition_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, competitionId);
            return ps.executeQuery().next();
        }
    }

    private int getMemberCount(Connection conn, int teamId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM team_members WHERE team_id = ?")) {
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    /** Generate a random 6-char uppercase invite code */
    private String generateCode() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6).toUpperCase();
    }

    private Team mapRow(ResultSet rs) throws SQLException {
        Team t = new Team();
        t.setId(rs.getInt("id"));
        t.setName(rs.getString("name"));
        t.setCompetitionId(rs.getInt("competition_id"));
        t.setLeaderId(rs.getInt("leader_id"));
        t.setLeaderName(rs.getString("leader_name"));
        t.setInviteCode(rs.getString("invite_code"));
        t.setCreatedAt(rs.getTimestamp("created_at"));
        t.setMemberCount(rs.getInt("member_count"));
        try {
            t.setCompetitionTitle(rs.getString("comp_title"));
        } catch (SQLException ignored) {
        }
        return t;
    }

    public boolean hasTeamSubmitted(int teamId, int competitionId) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT id FROM submissions WHERE team_id=? AND competition_id=?")) {

            ps.setInt(1, teamId);
            ps.setInt(2, competitionId);

            return ps.executeQuery().next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getTeamIdByUserAndCompetition(int userId, int competitionId) {
        String sql = "SELECT t.id FROM teams t " +
                "JOIN team_members tm ON t.id = tm.team_id " +
                "WHERE tm.user_id = ? AND t.competition_id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, competitionId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0; // no team
    }
}
