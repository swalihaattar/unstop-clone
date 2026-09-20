package com.unstop.dao;

import com.unstop.model.Competition;
import com.unstop.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CompetitionDAO - UPDATED from v1.
 * Change: BASE_SELECT now includes c.competition_type
 * Change: mapRow now sets competitionType
 * Change: addCompetition now accepts and stores competition_type
 * All other methods identical.
 */
public class CompetitionDAO {

    private static final String BASE_SELECT = "SELECT c.*, c.quiz_locked, cat.name AS category_name, u.name AS organizer_name, "
            +
            "       (SELECT COUNT(*) FROM registrations r WHERE r.competition_id = c.id) AS reg_count " +
            "FROM competitions c " +
            "LEFT JOIN categories cat ON c.category_id = cat.id " +
            "LEFT JOIN users u ON c.organizer_id = u.id ";

    public List<Competition> getAllOpen() {
        return query(BASE_SELECT + "WHERE c.status = 'open' ORDER BY c.created_at DESC", null);
    }

    public List<Competition> getByCategory(int categoryId) {
        if (categoryId == 0)
            return getAllOpen();
        return query(
                BASE_SELECT + "WHERE c.status = 'open' AND c.category_id = ? ORDER BY c.created_at DESC",
                ps -> ps.setInt(1, categoryId));
    }

    public Competition getById(int id) {
        List<Competition> list = query(BASE_SELECT + "WHERE c.id = ?", ps -> ps.setInt(1, id));
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Competition> getByOrganizer(int organizerId) {
        return query(
                BASE_SELECT + "WHERE c.organizer_id = ? ORDER BY c.created_at DESC",
                ps -> ps.setInt(1, organizerId));
    }

    /** UPDATED: now stores competition_type */
    public int addCompetition(Competition comp) {
        String sql = "INSERT INTO competitions (title, description, category_id, organizer_id, " +
                "prize_pool, last_date, team_size_min, team_size_max, competition_type) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, comp.getTitle());
            ps.setString(2, comp.getDescription());
            ps.setInt(3, comp.getCategoryId());
            ps.setInt(4, comp.getOrganizerId());
            ps.setString(5, comp.getPrizePool());
            ps.setDate(6, comp.getLastDate());
            ps.setInt(7, comp.getTeamSizeMin());
            ps.setInt(8, comp.getTeamSizeMax());
            ps.setString(9, comp.getCompetitionType() != null ? comp.getCompetitionType() : "general");

            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next())
                return keys.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<String[]> getAllCategories() {
        List<String[]> cats = new ArrayList<>();
        String sql = "SELECT id, name FROM categories ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                cats.add(new String[] { rs.getString("id"), rs.getString("name") });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cats;
    }

    @FunctionalInterface
    interface ParamSetter {
        void set(PreparedStatement ps) throws SQLException;
    }

    private List<Competition> query(String sql, ParamSetter setter) {
        List<Competition> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            if (setter != null)
                setter.set(ps);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Competition mapRow(ResultSet rs) throws SQLException {
        Competition c = new Competition();
        c.setId(rs.getInt("id"));
        c.setTitle(rs.getString("title"));
        c.setDescription(rs.getString("description"));
        c.setCategoryId(rs.getInt("category_id"));
        c.setCategoryName(rs.getString("category_name"));
        c.setOrganizerId(rs.getInt("organizer_id"));
        c.setOrganizerName(rs.getString("organizer_name"));
        c.setPrizePool(rs.getString("prize_pool"));
        c.setLastDate(rs.getDate("last_date"));
        c.setTeamSizeMin(rs.getInt("team_size_min"));
        c.setTeamSizeMax(rs.getInt("team_size_max"));
        c.setStatus(rs.getString("status"));
        c.setCompetitionType(rs.getString("competition_type")); // ← NEW
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setRegistrationCount(rs.getInt("reg_count"));
        c.setCompetitionType(rs.getString("competition_type"));
        c.setQuizLocked(rs.getBoolean("quiz_locked"));
        return c;
    }

    public boolean isQuizLocked(int competitionId) {
        String sql = "SELECT quiz_locked FROM competitions WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, competitionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("quiz_locked");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void lockQuiz(int competitionId) {
        String sql = "UPDATE competitions SET quiz_locked = 1 WHERE id=?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, competitionId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
