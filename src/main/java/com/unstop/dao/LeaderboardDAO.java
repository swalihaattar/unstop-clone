package com.unstop.dao;

import com.unstop.util.DBConnection;

import java.sql.*;
import java.util.*;

/**
 * LeaderboardDAO - produces ranked lists for a competition.
 *
 * For quiz: ranks by score DESC, then submitted_at ASC (faster = higher)
 * For hackathon: ranks by submission score DESC (organizer-graded)
 *
 * Returns List<Map> with keys: rank, name, score, total, detail
 * — using a generic Map keeps the JSP flexible without needing extra beans.
 */
public class LeaderboardDAO {

    /**
     * Quiz leaderboard for a competition.
     * Only includes submitted (completed) attempts.
     */
    public List<Map<String, Object>> getQuizLeaderboard(int competitionId) {
        String sql = "SELECT u.name, qa.score, qa.total_marks, qa.submitted_at " +
                "FROM quiz_attempts qa JOIN users u ON qa.user_id = u.id " +
                "WHERE qa.competition_id = ? AND qa.status = 'submitted' " +
                "ORDER BY qa.score DESC, qa.submitted_at ASC";

        return fetchRanked(sql, competitionId, rs -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name", rs.getString("name"));
            row.put("score", rs.getInt("score"));
            row.put("total", rs.getInt("total_marks"));
            row.put("detail", rs.getTimestamp("submitted_at").toString().substring(0, 19));
            return row;
        });
    }

    /**
     * Hackathon leaderboard — only graded submissions appear.
     */
    public List<Map<String, Object>> getHackathonLeaderboard(int competitionId) {
        String sql = "SELECT u.name, t.name AS team_name, s.project_title, s.score, s.feedback " +
                "FROM submissions s " +
                "JOIN users u ON s.user_id = u.id " +
                "LEFT JOIN teams t ON s.team_id = t.id " +
                "WHERE s.competition_id = ? AND s.score IS NOT NULL " +
                "ORDER BY s.score DESC";

        return fetchRanked(sql, competitionId, rs -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name", rs.getString("name"));
            row.put("teamName", rs.getString("team_name"));
            row.put("projectTitle", rs.getString("project_title"));
            row.put("score", rs.getInt("score"));
            row.put("total", 100);
            row.put("feedback", rs.getString("feedback"));
            return row;
        });
    }

    // ── Generic rank builder ─────────────────────────────────────

    @FunctionalInterface
    interface RowMapper {
        Map<String, Object> map(ResultSet rs) throws SQLException;
    }

    private List<Map<String, Object>> fetchRanked(String sql, int competitionId, RowMapper mapper) {
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, competitionId);
            ResultSet rs = ps.executeQuery();
            int rank = 1;
            while (rs.next()) {
                Map<String, Object> row = mapper.map(rs);

                row.put("rank", Integer.valueOf(rank)); // 🔥 force proper type
                rank++;

                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
