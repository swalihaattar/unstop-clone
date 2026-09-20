package com.unstop.model;

import java.sql.Timestamp;
import java.util.List;

/**
 * QuizAttempt - one student's attempt at a quiz competition.
 * score / totalMarks are computed on submission by QuizDAO.
 */
public class QuizAttempt {

    private int       id;
    private int       userId;
    private String    userName;
    private int       competitionId;
    private String    competitionTitle;
    private int       score;
    private int       totalMarks;
    private Timestamp startedAt;
    private Timestamp submittedAt;
    private String    status;           // in_progress | submitted
    private List<QuizQuestion> questions; // populated for result view

    public QuizAttempt() {}

    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }

    public int getUserId()                          { return userId; }
    public void setUserId(int u)                    { this.userId = u; }

    public String getUserName()                     { return userName; }
    public void setUserName(String n)               { this.userName = n; }

    public int getCompetitionId()                   { return competitionId; }
    public void setCompetitionId(int c)             { this.competitionId = c; }

    public String getCompetitionTitle()             { return competitionTitle; }
    public void setCompetitionTitle(String t)       { this.competitionTitle = t; }

    public int getScore()                           { return score; }
    public void setScore(int s)                     { this.score = s; }

    public int getTotalMarks()                      { return totalMarks; }
    public void setTotalMarks(int t)                { this.totalMarks = t; }

    public Timestamp getStartedAt()                 { return startedAt; }
    public void setStartedAt(Timestamp t)           { this.startedAt = t; }

    public Timestamp getSubmittedAt()               { return submittedAt; }
    public void setSubmittedAt(Timestamp t)         { this.submittedAt = t; }

    public String getStatus()                       { return status; }
    public void setStatus(String s)                 { this.status = s; }

    public List<QuizQuestion> getQuestions()        { return questions; }
    public void setQuestions(List<QuizQuestion> q)  { this.questions = q; }

    // Helper for JSP: percentage score
    public int getPercentage() {
        if (totalMarks == 0) return 0;
        return (score * 100) / totalMarks;
    }
}
