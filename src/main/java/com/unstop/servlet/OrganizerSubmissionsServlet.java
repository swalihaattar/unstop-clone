package com.unstop.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import com.unstop.dao.SubmissionDAO;
import javax.servlet.ServletException;
import com.unstop.model.Submission;
import java.util.List;

@WebServlet("/organizer/submissions")
public class OrganizerSubmissionsServlet extends HttpServlet {

    private SubmissionDAO submissionDAO = new SubmissionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        int competitionId = Integer.parseInt(req.getParameter("competitionId"));

        List<Submission> submissions = submissionDAO.getByCompetition(competitionId);

        req.setAttribute("submissions", submissions);
        req.getRequestDispatcher("/views/organizer-submissions.jsp")
                .forward(req, res);
    }
}