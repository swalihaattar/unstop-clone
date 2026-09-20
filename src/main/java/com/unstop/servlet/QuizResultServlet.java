package com.unstop.servlet;

import com.unstop.dao.QuizDAO;
import com.unstop.model.QuizAttempt;
import com.unstop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/** GET /quiz/result?attemptId=X → show score breakdown */
@WebServlet("/quiz/result")
public class QuizResultServlet extends HttpServlet {

    private final QuizDAO quizDAO = new QuizDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int attemptId = Integer.parseInt(req.getParameter("attemptId"));
        QuizAttempt attempt = quizDAO.getAttemptResult(attemptId);

        if (attempt == null) {
            res.sendError(404, "Result not found");
            return;
        }

        req.setAttribute("attempt", attempt);
        req.getRequestDispatcher("/views/quiz-result.jsp").forward(req, res);
    }
}
