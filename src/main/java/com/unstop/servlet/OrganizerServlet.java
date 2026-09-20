package com.unstop.servlet;

import com.unstop.dao.CompetitionDAO;
import com.unstop.dao.QuizDAO;
import com.unstop.dao.SubmissionDAO;
import com.unstop.model.Competition;
import com.unstop.model.QuizQuestion;
import com.unstop.model.Submission;
import com.unstop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.util.*;

/**
 * OrganizerServlet - UPDATED from v1.
 *
 * Routes handled (all require organizer role):
 * GET/POST /organizer/dashboard → list competitions + post new one
 * POST /organizer/question → add a quiz question
 * POST /organizer/grade → grade a hackathon submission
 */
@WebServlet({ "/organizer/dashboard", "/organizer/question", "/organizer/grade", "/organizer/finalizeQuiz" })
public class OrganizerServlet extends HttpServlet {

    private final CompetitionDAO competitionDAO = new CompetitionDAO();
    private final QuizDAO quizDAO = new QuizDAO();
    private final SubmissionDAO submissionDAO = new SubmissionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User organizer = getOrganizer(req, res);
        if (organizer == null)
            return;

        List<Competition> myComps = competitionDAO.getByOrganizer(organizer.getId());
        List<String[]> categories = competitionDAO.getAllCategories();

        // Build submissionsMap: competitionId → List<Submission> for hackathons
        Map<Integer, List<Submission>> submissionsMap = new HashMap<>();
        for (Competition c : myComps) {
            if (c.isHackathon()) {
                submissionsMap.put(c.getId(), submissionDAO.getByCompetition(c.getId()));
            }
        }

        Map<Integer, Integer> questionCountMap = new HashMap<>();

        for (Competition c : myComps) {
            if (c.isQuiz()) {
                int count = quizDAO.getQuestionCount(c.getId());
                questionCountMap.put(c.getId(), count);
            }
        }

        req.setAttribute("myCompetitions", myComps);
        req.setAttribute("categories", categories);
        req.setAttribute("submissionsMap", submissionsMap);

        req.setAttribute("questionCountMap", questionCountMap);
        req.getRequestDispatcher("/views/organizer-dashboard.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        User organizer = getOrganizer(req, res);
        if (organizer == null)
            return;

        String path = req.getServletPath();

        if ("/organizer/question".equals(path)) {
            handleAddQuestion(req, res, organizer);

        } else if ("/organizer/grade".equals(path)) {
            handleGrade(req, res);

        } else if ("/organizer/finalizeQuiz".equals(path)) {
            handleFinalizeQuiz(req, res);
        } else {
            handlePostCompetition(req, res, organizer);
        }
    }

    // ── Post new competition ──────────────────────────────────────
    private void handlePostCompetition(HttpServletRequest req, HttpServletResponse res, User organizer)
            throws ServletException, IOException {

        String title = req.getParameter("title").trim();
        String desc = req.getParameter("description").trim();
        int catId = Integer.parseInt(req.getParameter("categoryId"));
        String prize = req.getParameter("prizePool").trim();
        String lastDate = req.getParameter("lastDate");
        int minTeam = Integer.parseInt(req.getParameter("teamSizeMin"));
        int maxTeam = Integer.parseInt(req.getParameter("teamSizeMax"));
        String type = req.getParameter("competitionType");

        if (title.isEmpty() || desc.isEmpty() || lastDate.isEmpty()) {
            req.setAttribute("error", "Title, description and deadline are required.");
            doGet(req, res);
            return;
        }

        Competition comp = new Competition();
        comp.setTitle(title);
        comp.setDescription(desc);
        comp.setCategoryId(catId);
        comp.setOrganizerId(organizer.getId());
        comp.setPrizePool(prize);
        comp.setLastDate(Date.valueOf(lastDate));
        comp.setTeamSizeMin(minTeam);
        comp.setTeamSizeMax(maxTeam);
        comp.setCompetitionType(type);

        int newId = competitionDAO.addCompetition(comp);
        if (newId > 0) {
            res.sendRedirect(req.getContextPath() + "/organizer/dashboard?posted=true");
        } else {
            req.setAttribute("error", "Failed to post. Try again.");
            doGet(req, res);
        }
    }

    // ── Add a quiz question ───────────────────────────────────────
    private void handleAddQuestion(HttpServletRequest req, HttpServletResponse res, User organizer)
            throws IOException, ServletException {

        int competitionId = Integer.parseInt(req.getParameter("competitionId"));

        boolean quizLocked = competitionDAO.isQuizLocked(competitionId);
        req.setAttribute("quizLocked", quizLocked);

        String questionText = req.getParameter("questionText").trim();
        String optA = req.getParameter("optionA").trim();
        String optB = req.getParameter("optionB").trim();
        String optC = req.getParameter("optionC").trim();
        String optD = req.getParameter("optionD").trim();
        String correct = req.getParameter("correctOption");
        int marks = Integer.parseInt(req.getParameter("marks"));

        if (questionText.isEmpty()) {
            req.setAttribute("error", "Question text is required.");
            doGet(req, res);
            return;
        }

        QuizQuestion q = new QuizQuestion();
        q.setCompetitionId(competitionId);
        q.setQuestionText(questionText);
        q.setOptionA(optA);
        q.setOptionB(optB);
        q.setOptionC(optC);
        q.setOptionD(optD);
        q.setCorrectOption(correct);
        q.setMarks(marks);

        q.setOrderNum(quizDAO.getQuestionCount(competitionId) + 1);

        quizDAO.addQuestion(q);

        res.sendRedirect(req.getContextPath() + "/organizer/dashboard?question=true");
    }

    // ── Grade a hackathon submission ──────────────────────────────
    private void handleGrade(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        int submissionId = Integer.parseInt(req.getParameter("submissionId"));
        int score = Integer.parseInt(req.getParameter("score"));
        String feedback = req.getParameter("feedback");

        submissionDAO.grade(submissionId, score, feedback);
        res.sendRedirect(req.getContextPath() + "/organizer/dashboard?graded=true");
    }

    // ── Guard ────────────────────────────────────────────────────
    private User getOrganizer(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        User user = (User) session.getAttribute("user");
        if (!user.isOrganizer() && !user.isAdmin()) {
            res.sendRedirect(req.getContextPath() + "/competitions");
            return null;
        }
        return user;
    }

    private void handleFinalizeQuiz(HttpServletRequest req, HttpServletResponse res)
            throws IOException {

        int competitionId = Integer.parseInt(req.getParameter("competitionId"));

        competitionDAO.lockQuiz(competitionId);

        res.sendRedirect(req.getContextPath() + "/organizer/dashboard?finalized=true");
    }
}
