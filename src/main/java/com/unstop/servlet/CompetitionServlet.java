package com.unstop.servlet;

import com.unstop.dao.CompetitionDAO;
import com.unstop.dao.QuizDAO;
import com.unstop.dao.RegistrationDAO;
import com.unstop.model.Competition;
import com.unstop.model.QuizAttempt;
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
 * CompetitionServlet - UPDATED from v1.
 * showDetail() now also fetches the user's quiz attempt so the
 * detail JSP can show "Start Quiz" vs "View Result" correctly.
 */
@WebServlet("/competitions")
public class CompetitionServlet extends HttpServlet {

    private final CompetitionDAO competitionDAO = new CompetitionDAO();
    private final RegistrationDAO registrationDAO = new RegistrationDAO();
    private final QuizDAO quizDAO = new QuizDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");
        if (idParam != null) {
            showDetail(req, res, Integer.parseInt(idParam));
        } else {
            showListing(req, res);
        }
    }

    private void showListing(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setAttribute("competitions", competitionDAO.getAllOpen());
        req.setAttribute("categories", competitionDAO.getAllCategories());
        req.getRequestDispatcher("/views/competitions.jsp").forward(req, res);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse res, int id)
            throws ServletException, IOException {

        Competition competition = competitionDAO.getById(id);
        if (competition == null) {
            res.sendError(404);
            return;
        }

        HttpSession session = req.getSession(false);
        boolean alreadyRegistered = false;

        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            alreadyRegistered = registrationDAO.isRegistered(user.getId(), id);

            // For quiz competitions, pass the user's attempt status to JSP
            if (competition.isQuiz() && alreadyRegistered) {
                QuizAttempt attempt = quizDAO.getAttemptByUser(user.getId(), id);
                req.setAttribute("quizAttempt", attempt);
            }
        }

        req.setAttribute("competition", competition);
        req.setAttribute("alreadyRegistered", alreadyRegistered);
        req.getRequestDispatcher("/views/competition-detail.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int competitionId = Integer.parseInt(req.getParameter("competitionId"));
        User user = (User) session.getAttribute("user");
        boolean success = registrationDAO.register(user.getId(), competitionId);

        String redirect = req.getContextPath() + "/competitions?id=" + competitionId +
                (success ? "&registered=true" : "&alreadyRegistered=true");
        res.sendRedirect(redirect);
    }
}
