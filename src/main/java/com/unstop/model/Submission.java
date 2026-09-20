package com.unstop.model;

import java.sql.Timestamp;

/**
 * Submission - a team/individual hackathon project submission.
 * Organizers can later assign a score and feedback.
 */
public class Submission {

    private int       id;
    private int       competitionId;
    private String    competitionTitle;
    private int       userId;
    private String    userName;
    private Integer   teamId;           // nullable - solo submissions have no team
    private String    teamName;
    private String    projectTitle;
    private String    description;
    private String    githubUrl;
    private String    demoUrl;
    private Timestamp submittedAt;
    private Integer   score;            // nullable until organizer grades
    private String    feedback;

    public Submission() {}

    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }

    public int getCompetitionId()                   { return competitionId; }
    public void setCompetitionId(int c)             { this.competitionId = c; }

    public String getCompetitionTitle()             { return competitionTitle; }
    public void setCompetitionTitle(String t)       { this.competitionTitle = t; }

    public int getUserId()                          { return userId; }
    public void setUserId(int u)                    { this.userId = u; }

    public String getUserName()                     { return userName; }
    public void setUserName(String n)               { this.userName = n; }

    public Integer getTeamId()                      { return teamId; }
    public void setTeamId(Integer t)                { this.teamId = t; }

    public String getTeamName()                     { return teamName; }
    public void setTeamName(String n)               { this.teamName = n; }

    public String getProjectTitle()                 { return projectTitle; }
    public void setProjectTitle(String t)           { this.projectTitle = t; }

    public String getDescription()                  { return description; }
    public void setDescription(String d)            { this.description = d; }

    public String getGithubUrl()                    { return githubUrl; }
    public void setGithubUrl(String u)              { this.githubUrl = u; }

    public String getDemoUrl()                      { return demoUrl; }
    public void setDemoUrl(String u)                { this.demoUrl = u; }

    public Timestamp getSubmittedAt()               { return submittedAt; }
    public void setSubmittedAt(Timestamp t)         { this.submittedAt = t; }

    public Integer getScore()                       { return score; }
    public void setScore(Integer s)                 { this.score = s; }

    public String getFeedback()                     { return feedback; }
    public void setFeedback(String f)               { this.feedback = f; }

    public boolean isGraded()                       { return score != null; }
}
