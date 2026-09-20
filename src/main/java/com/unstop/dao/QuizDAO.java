package com.unstop.dao;

import com.unstop.model.QuizAttempt;
import com.unstop.model.QuizQuestion;
import com.unstop.util.DBConnection;

import java.sql.*;
import java.util.*;

/**
 * QuizDAO - all quiz-related DB operations.
 *
 * Flow:
 * 1. organizer calls addQuestion() for each MCQ
 * 2. student calls startAttempt() → gets attempt id
 * 3. student calls getQuestions() → gets questions WITHOUT correct answers
 * 4. on submit: submitAttempt() receives Map<questionId, chosenOption>
 * → scores each answer, updates quiz_attempts.score
 * 5. getAttemptWithAnswers() returns full result for the result page
 */
public class QuizDAO {

    // ── Organizer: add a question ────────────────────────────────
    public boolean addQuestion(QuizQuestion q) {
        String sql = "INSERT INTO quiz_questions " +
                "(competition_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks, order_num) "
                +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, q.getCompetitionId());
            ps.setString(2, q.getQuestionText());
            ps.setString(3, q.getOptionA());
            ps.setString(4, q.getOptionB());
            ps.setString(5, q.getOptionC());
            ps.setString(6, q.getOptionD());
            ps.setString(7, q.getCorrectOption());
            ps.setInt(8, q.getMarks());
            ps.setInt(9, q.getOrderNum());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get questions for display — correctOption is intentionally excluded
     * so it is never sent to the browser.
     */
    public List<QuizQuestion> getQuestions(int competitionId) {
        String sql = "SELECT id, competition_id, question_text, option_a, option_b, option_c, option_d, marks, order_num "
                +
                "FROM quiz_questions WHERE competition_id = ? ORDER BY order_num";
        List<QuizQuestion> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, competitionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                QuizQuestion q = new QuizQuestion();
                q.setId(rs.getInt("id"));
                q.setCompetitionId(rs.getInt("competition_id"));
                q.setQuestionText(rs.getString("question_text"));
                q.setOptionA(rs.getString("option_a"));
                q.setOptionB(rs.getString("option_b"));
                q.setOptionC(rs.getString("option_c"));
                q.setOptionD(rs.getString("option_d"));
                q.setMarks(rs.getInt("marks"));
                q.setOrderNum(rs.getInt("order_num"));
                list.add(q);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Start a new quiz attempt. Returns attempt id.
     * If the student already has an attempt, returns the existing id.
     */
    public int startAttempt(int userId, int competitionId) {
        // Check for existing attempt first
        String check = "SELECT id FROM quiz_attempts WHERE user_id=? AND competition_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(check)) {
            ps.setInt(1, userId);
            ps.setInt(2, competitionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        // Create new attempt
        String sql = "INSERT INTO quiz_attempts (user_id, competition_id, status) VALUES (?,?,'in_progress')";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setInt(2, competitionId);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            return keys.next() ? keys.getInt(1) : -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Submit the quiz. Scores each answer and saves to quiz_answers.
     * Returns the completed QuizAttempt with score.
     *
     * @param attemptId the attempt id
     * @param answers   Map of questionId → chosen option ("A","B","C","D")
     */
    public QuizAttempt submitAttempt(int attemptId, Map<Integer, String> answers) {
        // Fetch correct answers for all questions in this attempt
        String fetchCorrect = "SELECT qq.id, qq.correct_option, qq.marks " +
                "FROM quiz_questions qq " +
                "JOIN quiz_attempts qa ON qq.competition_id = qa.competition_id " +
                "WHERE qa.id = ?";

        int totalScore = 0;
        int totalMarks = 0;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            Map<Integer, String> correctMap = new HashMap<>();
            Map<Integer, Integer> marksMap = new HashMap<>();

            try (PreparedStatement ps = conn.prepareStatement(fetchCorrect)) {
                ps.setInt(1, attemptId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int qid = rs.getInt("id");
                    correctMap.put(qid, rs.getString("correct_option"));
                    marksMap.put(qid, rs.getInt("marks"));
                    totalMarks += rs.getInt("marks");
                }
            }

            // Insert one quiz_answer row per question
            String insertAns = "INSERT INTO quiz_answers (attempt_id, question_id, chosen, is_correct) VALUES (?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(insertAns)) {
                for (Map.Entry<Integer, String> e : answers.entrySet()) {
                    int qid = e.getKey();
                    String chosen = e.getValue();
                    boolean correct = chosen != null && chosen.equals(correctMap.get(qid));
                    if (correct)
                        totalScore += marksMap.getOrDefault(qid, 1);

                    ps.setInt(1, attemptId);
                    ps.setInt(2, qid);
                    ps.setString(3, chosen);
                    ps.setBoolean(4, correct);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // Update attempt with score and submitted_at
            String update = "UPDATE quiz_attempts SET score=?, total_marks=?, status='submitted', submitted_at=NOW() WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(update)) {
                ps.setInt(1, totalScore);
                ps.setInt(2, totalMarks);
                ps.setInt(3, attemptId);
                ps.executeUpdate();
            }

            conn.commit();

            // Return populated attempt bean
            QuizAttempt attempt = new QuizAttempt();
            attempt.setId(attemptId);
            attempt.setScore(totalScore);
            attempt.setTotalMarks(totalMarks);
            attempt.setStatus("submitted");
            return attempt;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get a completed attempt with full question+answer breakdown for result page.
     */
    public QuizAttempt getAttemptResult(int attemptId) {
        String sql = "SELECT qa.*, u.name AS user_name, c.title AS comp_title " +
                "FROM quiz_attempts qa " +
                "JOIN users u ON qa.user_id = u.id " +
                "JOIN competitions c ON qa.competition_id = c.id " +
                "WHERE qa.id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attemptId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next())
                return null;

            QuizAttempt a = new QuizAttempt();
            a.setId(rs.getInt("id"));
            a.setUserId(rs.getInt("user_id"));
            a.setUserName(rs.getString("user_name"));
            a.setCompetitionId(rs.getInt("competition_id"));
            a.setCompetitionTitle(rs.getString("comp_title"));
            a.setScore(rs.getInt("score"));
            a.setTotalMarks(rs.getInt("total_marks"));
            a.setStatus(rs.getString("status"));
            a.setStartedAt(rs.getTimestamp("started_at"));
            a.setSubmittedAt(rs.getTimestamp("submitted_at"));

            // Load questions with student's chosen answers
            a.setQuestions(getQuestionsWithAnswers(conn, attemptId));
            return a;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get attempt by user + competition (to check if already submitted).
     */
    public QuizAttempt getAttemptByUser(int userId, int competitionId) {
        String sql = "SELECT * FROM quiz_attempts WHERE user_id=? AND competition_id=?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, competitionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                QuizAttempt a = new QuizAttempt();
                a.setId(rs.getInt("id"));
                a.setScore(rs.getInt("score"));
                a.setTotalMarks(rs.getInt("total_marks"));
                a.setStatus(rs.getString("status"));
                return a;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<QuizQuestion> getQuestionsWithAnswers(Connection conn, int attemptId) throws SQLException {
        String sql = "SELECT qq.*, ans.chosen, ans.is_correct " +
                "FROM quiz_answers ans " +
                "JOIN quiz_questions qq ON ans.question_id = qq.id " +
                "WHERE ans.attempt_id = ? ORDER BY qq.order_num";
        List<QuizQuestion> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attemptId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                QuizQuestion q = new QuizQuestion();
                q.setId(rs.getInt("id"));
                q.setQuestionText(rs.getString("question_text"));
                q.setOptionA(rs.getString("option_a"));
                q.setOptionB(rs.getString("option_b"));
                q.setOptionC(rs.getString("option_c"));
                q.setOptionD(rs.getString("option_d"));
                q.setCorrectOption(rs.getString("correct_option"));
                q.setMarks(rs.getInt("marks"));
                q.setChosenOption(rs.getString("chosen"));
                q.setCorrect(rs.getBoolean("is_correct"));
                list.add(q);
            }
        }
        return list;
    }

    public int getQuestionCount(int competitionId) {
        String sql = "SELECT COUNT(*) FROM quiz_questions WHERE competition_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, competitionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getAttemptCount(int competitionId) {
        String sql = "SELECT COUNT(*) FROM quiz_attempts WHERE competition_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, competitionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<QuizAttempt> getAttemptsByUser(int userId) {
        String sql = "SELECT qa.*, c.title AS comp_title " +
                "FROM quiz_attempts qa " +
                "JOIN competitions c ON qa.competition_id = c.id " +
                "WHERE qa.user_id = ? AND qa.status = 'submitted' " +
                "ORDER BY qa.submitted_at DESC";

        List<QuizAttempt> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                QuizAttempt a = new QuizAttempt();
                a.setId(rs.getInt("id"));
                a.setUserId(userId);
                a.setCompetitionId(rs.getInt("competition_id"));
                a.setCompetitionTitle(rs.getString("comp_title"));
                a.setScore(rs.getInt("score"));
                a.setTotalMarks(rs.getInt("total_marks"));
                a.setStatus(rs.getString("status"));
                a.setSubmittedAt(rs.getTimestamp("submitted_at"));
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean isQuizLocked(int competitionId) {
        String sql = "SELECT quiz_locked FROM competitions WHERE id=?";
        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

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
}
