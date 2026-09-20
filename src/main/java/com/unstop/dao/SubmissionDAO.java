package com.unstop.dao;

import com.unstop.model.Submission;
import com.unstop.util.DBConnection;

import java.sql.*;
import java.util.*;

/**
 * SubmissionDAO - hackathon project submissions.
 */
public class SubmissionDAO {

    public boolean submit(Submission s) {
        String sql = "INSERT INTO submissions " +
                "(competition_id, user_id, team_id, project_title, description, github_url, demo_url) " +
                "VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getCompetitionId());
            ps.setInt(2, s.getUserId());
            if (s.getTeamId() != null)
                ps.setInt(3, s.getTeamId());
            else
                ps.setNull(3, Types.INTEGER);
            ps.setString(4, s.getProjectTitle());
            ps.setString(5, s.getDescription());
            ps.setString(6, s.getGithubUrl());
            ps.setString(7, s.getDemoUrl());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasSubmitted(int userId, int competitionId) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT id FROM submissions WHERE user_id=? AND competition_id=?")) {
            ps.setInt(1, userId);
            ps.setInt(2, competitionId);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** All submissions for a competition (organizer view) */
    public List<Submission> getByCompetition(int competitionId) {
        String sql = "SELECT s.*, u.name AS user_name, t.name AS team_name, c.title AS comp_title " +
                "FROM submissions s JOIN users u ON s.user_id = u.id " +
                "JOIN competitions c ON s.competition_id = c.id " +
                "LEFT JOIN teams t ON s.team_id = t.id " +
                "WHERE s.competition_id = ? ORDER BY s.submitted_at DESC";
        return query(sql, ps -> ps.setInt(1, competitionId));
    }

    /** A student's own submission for one competition */
    public Submission getByUser(int userId, int competitionId) {
        String sql = "SELECT s.*, u.name AS user_name, t.name AS team_name, c.title AS comp_title " +
                "FROM submissions s JOIN users u ON s.user_id = u.id " +
                "JOIN competitions c ON s.competition_id = c.id " +
                "LEFT JOIN teams t ON s.team_id = t.id " +
                "WHERE s.user_id=? AND s.competition_id=?";
        List<Submission> r = query(sql, ps -> {
            ps.setInt(1, userId);
            ps.setInt(2, competitionId);
        });
        return r.isEmpty() ? null : r.get(0);
    }

    /** Organizer grades a submission */
    public boolean grade(int submissionId, int score, String feedback) {
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE submissions SET score=?, feedback=? WHERE id=?")) {
            ps.setInt(1, score);
            ps.setString(2, feedback);
            ps.setInt(3, submissionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<Submission> query(String sql, ParamSetter setter) {
        List<Submission> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.set(ps);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Submission s = new Submission();
                s.setId(rs.getInt("id"));
                s.setCompetitionId(rs.getInt("competition_id"));
                s.setCompetitionTitle(rs.getString("comp_title"));
                s.setUserId(rs.getInt("user_id"));
                s.setUserName(rs.getString("user_name"));
                s.setProjectTitle(rs.getString("project_title"));
                s.setDescription(rs.getString("description"));
                s.setGithubUrl(rs.getString("github_url"));
                s.setDemoUrl(rs.getString("demo_url"));
                s.setSubmittedAt(rs.getTimestamp("submitted_at"));
                int score = rs.getInt("score");
                if (!rs.wasNull())
                    s.setScore(score);
                s.setFeedback(rs.getString("feedback"));
                s.setTeamName(rs.getString("team_name"));
                int tid = rs.getInt("team_id");
                if (!rs.wasNull())
                    s.setTeamId(tid);
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** All submissions by a student (for their dashboard) */
    public List<Submission> getByUserAll(int userId) {
        String sql = "SELECT s.*, u.name AS user_name, t.name AS team_name, c.title AS comp_title " +
                "FROM submissions s JOIN users u ON s.user_id = u.id " +
                "JOIN competitions c ON s.competition_id = c.id " +
                "LEFT JOIN teams t ON s.team_id = t.id " +
                "WHERE s.user_id = ? ORDER BY s.submitted_at DESC";
        return query(sql, ps -> ps.setInt(1, userId));
    }

    @FunctionalInterface
    interface ParamSetter {
        void set(PreparedStatement ps) throws SQLException;
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

    public Submission getByTeam(int teamId, int competitionId) {
        String sql = "SELECT s.*, u.name AS user_name, t.name AS team_name, c.title AS comp_title " +
                "FROM submissions s JOIN users u ON s.user_id = u.id " +
                "JOIN competitions c ON s.competition_id = c.id " +
                "LEFT JOIN teams t ON s.team_id = t.id " +
                "WHERE s.team_id=? AND s.competition_id=?";

        List<Submission> r = query(sql, ps -> {
            ps.setInt(1, teamId);
            ps.setInt(2, competitionId);
        });

        return r.isEmpty() ? null : r.get(0);
    }
}
