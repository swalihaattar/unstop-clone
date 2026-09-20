package com.unstop.servlet;

import com.unstop.dao.CompetitionDAO;
import com.unstop.dao.LeaderboardDAO;
import com.unstop.model.Competition;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

/**
 * LeaderboardServlet - /leaderboard?competitionId=X
 * Reads competition type and delegates to the correct leaderboard query.
 */
@WebServlet("/leaderboard")
public class LeaderboardServlet extends HttpServlet {

    private final LeaderboardDAO  leaderboardDAO  = new LeaderboardDAO();
    private final CompetitionDAO  competitionDAO  = new CompetitionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        int competitionId = Integer.parseInt(req.getParameter("competitionId"));
        Competition comp  = competitionDAO.getById(competitionId);

        if (comp == null) { res.sendError(404); return; }

        List<Map<String, Object>> rankings;
        if ("quiz".equals(comp.getCompetitionType())) {
            rankings = leaderboardDAO.getQuizLeaderboard(competitionId);
        } else {
            rankings = leaderboardDAO.getHackathonLeaderboard(competitionId);
        }

        req.setAttribute("rankings", rankings);
        req.setAttribute("competition", comp);
        req.getRequestDispatcher("/views/leaderboard.jsp").forward(req, res);
    }
}
