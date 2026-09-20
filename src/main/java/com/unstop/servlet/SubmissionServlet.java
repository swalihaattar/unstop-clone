package com.unstop.servlet;

import com.unstop.dao.SubmissionDAO;
import com.unstop.dao.TeamDAO;
import com.unstop.model.Submission;
import com.unstop.model.Team;
import com.unstop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * SubmissionServlet - /submit
 * GET → show submission form (with team info if applicable)
 * POST → save submission to DB
 */
@WebServlet("/submit")
public class SubmissionServlet extends HttpServlet {

    private final SubmissionDAO submissionDAO = new SubmissionDAO();
    private final TeamDAO teamDAO = new TeamDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User user = requireLogin(req, res);
        if (user == null)
            return;

        int competitionId = Integer.parseInt(req.getParameter("competitionId"));

        int teamId = teamDAO.getTeamIdByUserAndCompetition(user.getId(), competitionId);

        if (teamId == 0) {
            req.setAttribute("error", "You must be in a team to submit.");
            doGet(req, res);
            return;
        }

        Submission existing = submissionDAO.getByTeam(teamId, competitionId);
        boolean already = submissionDAO.hasTeamSubmitted(teamId, competitionId);

        req.setAttribute("submission", existing);
        req.setAttribute("alreadySubmitted", already);

        // Attach team if user has one
        Team team = teamDAO.getTeamForUser(user.getId(), competitionId);
        req.setAttribute("team", team);
        req.setAttribute("competitionId", competitionId);
        req.getRequestDispatcher("/views/submission.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User user = requireLogin(req, res);
        if (user == null)
            return;

        int competitionId = Integer.parseInt(req.getParameter("competitionId"));
        String projectTitle = req.getParameter("projectTitle").trim();
        String description = req.getParameter("description").trim();
        String githubUrl = req.getParameter("githubUrl").trim();
        String demoUrl = req.getParameter("demoUrl").trim();

        int teamId = teamDAO.getTeamIdByUserAndCompetition(user.getId(), competitionId);

        if (teamId == 0) {
            req.setAttribute("error", "You must be in a team to submit.");
            doGet(req, res);
            return;
        }
        // Already submitted check (TEAM BASED)
        if (submissionDAO.hasTeamSubmitted(teamId, competitionId)) {
            req.setAttribute("error", "Your team has already submitted!");
            doGet(req, res);
            return;
        }

        // validation
        if (projectTitle.isEmpty()) {
            req.setAttribute("error", "Project title is required.");
            doGet(req, res);
            return;
        }

        // Create submission
        Submission s = new Submission();
        s.setCompetitionId(competitionId);
        s.setUserId(user.getId()); // keep for organizer reference
        s.setTeamId(teamId);
        s.setProjectTitle(projectTitle);
        s.setDescription(description);
        s.setGithubUrl(githubUrl.isEmpty() ? null : githubUrl);
        s.setDemoUrl(demoUrl.isEmpty() ? null : demoUrl);

        boolean ok = submissionDAO.submit(s);

        if (ok) {
            res.sendRedirect(req.getContextPath() + "/submit?competitionId=" + competitionId + "&submitted=true");
        } else {
            req.setAttribute("error", "Submission failed.");
            doGet(req, res);
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
