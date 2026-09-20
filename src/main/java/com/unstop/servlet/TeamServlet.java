package com.unstop.servlet;

import com.unstop.dao.TeamDAO;
import com.unstop.model.Team;
import com.unstop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * TeamServlet - /teams
 * GET  → show team page (create form + join form + current team)
 * POST → action=create or action=join
 */
@WebServlet("/teams")
public class TeamServlet extends HttpServlet {

    private final TeamDAO teamDAO = new TeamDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User user = requireLogin(req, res);
        if (user == null) return;

        int  competitionId = Integer.parseInt(req.getParameter("competitionId"));
        Team myTeam        = teamDAO.getTeamForUser(user.getId(), competitionId);

        if (myTeam != null) {
            myTeam.setMemberNames(teamDAO.getMemberNames(myTeam.getId()));
        }

        req.setAttribute("myTeam", myTeam);
        req.setAttribute("competitionId", competitionId);
        req.getRequestDispatcher("/views/teams.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User user = requireLogin(req, res);
        if (user == null) return;

        String action        = req.getParameter("action");
        int    competitionId = Integer.parseInt(req.getParameter("competitionId"));

        if ("create".equals(action)) {
            String teamName = req.getParameter("teamName").trim();
            if (teamName.isEmpty()) {
                req.setAttribute("error", "Team name is required.");
                doGet(req, res);
                return;
            }
            int newId = teamDAO.createTeam(teamName, competitionId, user.getId());
            if (newId > 0) {
                res.sendRedirect(req.getContextPath() + "/teams?competitionId=" + competitionId + "&created=true");
            } else {
                req.setAttribute("error", "Could not create team. You may already be in a team.");
                doGet(req, res);
            }

        } else if ("join".equals(action)) {
            String code   = req.getParameter("inviteCode").trim().toUpperCase();
            String result = teamDAO.joinTeam(code, user.getId());

            switch (result) {
                case "ok":
                    res.sendRedirect(req.getContextPath() + "/teams?competitionId=" + competitionId + "&joined=true");
                    break;
                case "not_found":
                    req.setAttribute("error", "Invalid invite code.");
                    doGet(req, res); break;
                case "already_in_team":
                    req.setAttribute("error", "You are already in a team for this competition.");
                    doGet(req, res); break;
                case "team_full":
                    req.setAttribute("error", "That team is already full.");
                    doGet(req, res); break;
                default:
                    req.setAttribute("error", "Something went wrong. Try again.");
                    doGet(req, res);
            }
        }
    }

    private User requireLogin(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login"); return null;
        }
        return (User) session.getAttribute("user");
    }
}
