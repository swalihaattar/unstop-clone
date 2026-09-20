package com.unstop.model;

import java.sql.Timestamp;

/**
 * Registration - UPDATED from v1.
 * Added: competitionType field used by dashboard.jsp
 */
public class Registration {

    private int       id;
    private int       userId;
    private int       competitionId;
    private Timestamp registeredAt;
    private String    userName;
    private String    competitionTitle;
    private String    competitionStatus;
    private String    competitionType;   // ← NEW: general | quiz | hackathon

    public Registration() {}

    public Registration(int userId, int competitionId) {
        this.userId = userId; this.competitionId = competitionId;
    }

    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }

    public int getUserId()                          { return userId; }
    public void setUserId(int u)                    { this.userId = u; }

    public int getCompetitionId()                   { return competitionId; }
    public void setCompetitionId(int id)            { this.competitionId = id; }

    public Timestamp getRegisteredAt()              { return registeredAt; }
    public void setRegisteredAt(Timestamp t)        { this.registeredAt = t; }

    public String getUserName()                     { return userName; }
    public void setUserName(String n)               { this.userName = n; }

    public String getCompetitionTitle()             { return competitionTitle; }
    public void setCompetitionTitle(String t)       { this.competitionTitle = t; }

    public String getCompetitionStatus()            { return competitionStatus; }
    public void setCompetitionStatus(String s)      { this.competitionStatus = s; }

    public String getCompetitionType()              { return competitionType; }
    public void setCompetitionType(String t)        { this.competitionType = t; }
}
