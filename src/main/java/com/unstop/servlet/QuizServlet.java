package com.unstop.servlet;

import com.unstop.dao.QuizDAO;
import com.unstop.dao.RegistrationDAO;
import com.unstop.model.QuizAttempt;
import com.unstop.model.QuizQuestion;
import com.unstop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * QuizServlet - Controller for /quiz
 *
 * GET /quiz?competitionId=X → start or resume quiz, show questions
 * POST /quiz → submit answers, compute score, redirect to result
 *
 * SECURITY:
 * - Must be registered for the competition to access
 * - Can only attempt once (startAttempt returns existing id if already started)
 * - correctOption is NEVER sent to the JSP (fetched by getQuestions which omits
 * it)
 */
@WebServlet("/quiz")
public class QuizServlet extends HttpServlet {

    private final QuizDAO quizDAO = new QuizDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User user = requireLogin(req, res);
        if (user == null)
            return;

        int competitionId = Integer.parseInt(req.getParameter("competitionId"));

        // 🚫 Block if quiz not finalized
        if (!quizDAO.isQuizLocked(competitionId)) {
            res.sendRedirect(req.getContextPath() + "/competitions?id=" + competitionId + "&error=quiz_not_live");
            return;
        }
        // Must be registered
        if (!registrationDAO.isRegistered(user.getId(), competitionId)) {
            res.sendRedirect(req.getContextPath() + "/competitions?id=" + competitionId);
            return;
        }

        // Check if already submitted
        QuizAttempt existing = quizDAO.getAttemptByUser(user.getId(), competitionId);
        if (existing != null && "submitted".equals(existing.getStatus())) {
            res.sendRedirect(req.getContextPath() + "/quiz/result?attemptId=" + existing.getId());
            return;
        }

        // Start or resume attempt
        int attemptId = quizDAO.startAttempt(user.getId(), competitionId);

        // Get questions (no correct answers included)
        List<QuizQuestion> questions = quizDAO.getQuestions(competitionId);

        boolean quizLocked = quizDAO.isQuizLocked(competitionId);
        req.setAttribute("quizLocked", quizLocked);
        req.setAttribute("questions", questions);
        req.setAttribute("attemptId", attemptId);
        req.setAttribute("competitionId", competitionId);
        req.setAttribute("questionCount", questions.size());

        req.getRequestDispatcher("/views/quiz.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User user = requireLogin(req, res);
        if (user == null)
            return;

        int attemptId = Integer.parseInt(req.getParameter("attemptId"));

        // Collect answers: form fields are named "q_<questionId>"
        Map<Integer, String> answers = new HashMap<>();
        Enumeration<String> params = req.getParameterNames();
        while (params.hasMoreElements()) {
            String name = params.nextElement();
            if (name.startsWith("q_")) {
                int questionId = Integer.parseInt(name.substring(2));
                answers.put(questionId, req.getParameter(name).toUpperCase());
            }
        }

        QuizAttempt result = quizDAO.submitAttempt(attemptId, answers);

        if (result != null) {
            res.sendRedirect(req.getContextPath() + "/quiz/result?attemptId=" + attemptId);
        } else {
            res.sendRedirect(req.getContextPath() + "/quiz?attemptId=" + attemptId + "&error=submit_failed");
        }
    }

    private User requireLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        return (User) session.getAttribute("user");
    }
}
