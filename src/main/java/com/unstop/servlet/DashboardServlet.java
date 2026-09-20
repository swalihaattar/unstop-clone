package com.unstop.servlet;

import com.unstop.dao.QuizDAO;
import com.unstop.dao.RegistrationDAO;
import com.unstop.dao.SubmissionDAO;
import com.unstop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * DashboardServlet - UPDATED from v1.
 * Now fetches registrations + quiz attempts + submissions and
 * passes all three to the dashboard JSP.
 *
 * RegistrationDAO.getByUser() now also returns competitionType
 * so the JSP can show the type column. (Requires a small JOIN
 * update in RegistrationDAO — see UPDATED note in that file.)
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private final QuizDAO quizDAO = new QuizDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (user.isOrganizer()) {
            res.sendRedirect(req.getContextPath() + "/organizer/dashboard");
            return;
        }

        req.setAttribute("myRegistrations", registrationDAO.getByUser(user.getId()));
        req.setAttribute("myAttempts", quizDAO.getAttemptsByUser(user.getId()));
        req.setAttribute("mySubmissions", submissionDAO.getByUserAll(user.getId()));

        req.getRequestDispatcher("/views/dashboard.jsp").forward(req, res);
    }
}
