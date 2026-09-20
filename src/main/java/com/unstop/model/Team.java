package com.unstop.model;

import java.sql.Timestamp;
import java.util.List;

/**
 * Team - JavaBean for the `teams` table.
 *
 * A team is created by a leader (student) for a specific competition.
 * Other students join using the 6-character invite_code.
 * members list is populated by TeamDAO when needed (not always loaded).
 */
public class Team {

    private int         id;
    private String      name;
    private int         competitionId;
    private String      competitionTitle;   // joined
    private int         leaderId;
    private String      leaderName;         // joined
    private String      inviteCode;
    private Timestamp   createdAt;
    private List<String> memberNames;       // populated on demand
    private int         memberCount;

    public Team() {}

    // ── Getters / Setters ──────────────────────────────────────
    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }

    public String getName()                         { return name; }
    public void setName(String name)                { this.name = name; }

    public int getCompetitionId()                   { return competitionId; }
    public void setCompetitionId(int id)            { this.competitionId = id; }

    public String getCompetitionTitle()             { return competitionTitle; }
    public void setCompetitionTitle(String t)       { this.competitionTitle = t; }

    public int getLeaderId()                        { return leaderId; }
    public void setLeaderId(int id)                 { this.leaderId = id; }

    public String getLeaderName()                   { return leaderName; }
    public void setLeaderName(String n)             { this.leaderName = n; }

    public String getInviteCode()                   { return inviteCode; }
    public void setInviteCode(String c)             { this.inviteCode = c; }

    public Timestamp getCreatedAt()                 { return createdAt; }
    public void setCreatedAt(Timestamp t)           { this.createdAt = t; }

    public List<String> getMemberNames()            { return memberNames; }
    public void setMemberNames(List<String> m)      { this.memberNames = m; }

    public int getMemberCount()                     { return memberCount; }
    public void setMemberCount(int n)               { this.memberCount = n; }
}
