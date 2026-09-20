package com.unstop.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Competition - JavaBean. UPDATED from v1.
 * Added: competitionType ("general" | "quiz" | "hackathon")
 * Everything else identical to v1.
 */
public class Competition {

    private int id;
    private String title;
    private String description;
    private int categoryId;
    private String categoryName;
    private int organizerId;
    private String organizerName;
    private String prizePool;
    private Date lastDate;
    private int teamSizeMin;
    private int teamSizeMax;
    private String status;
    private String competitionType; // ← NEW: general | quiz | hackathon
    private Timestamp createdAt;
    private int registrationCount;
    private boolean quizLocked;

    public boolean isQuizLocked() {
        return quizLocked;
    }

    public void setQuizLocked(boolean quizLocked) {
        this.quizLocked = quizLocked;
    }

    public Competition() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        this.description = d;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int c) {
        this.categoryId = c;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String n) {
        this.categoryName = n;
    }

    public int getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(int id) {
        this.organizerId = id;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String n) {
        this.organizerName = n;
    }

    public String getPrizePool() {
        return prizePool;
    }

    public void setPrizePool(String p) {
        this.prizePool = p;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date d) {
        this.lastDate = d;
    }

    public int getTeamSizeMin() {
        return teamSizeMin;
    }

    public void setTeamSizeMin(int n) {
        this.teamSizeMin = n;
    }

    public int getTeamSizeMax() {
        return teamSizeMax;
    }

    public void setTeamSizeMax(int n) {
        this.teamSizeMax = n;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String s) {
        this.status = s;
    }

    public String getCompetitionType() {
        return competitionType;
    }

    public void setCompetitionType(String t) {
        this.competitionType = t;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp t) {
        this.createdAt = t;
    }

    public int getRegistrationCount() {
        return registrationCount;
    }

    public void setRegistrationCount(int n) {
        this.registrationCount = n;
    }

    public String getTeamSizeDisplay() {
        if (teamSizeMin == teamSizeMax)
            return String.valueOf(teamSizeMin);
        return teamSizeMin + " - " + teamSizeMax;
    }

    // Helper booleans for JSP type-based rendering
    public boolean isQuiz() {
        return "quiz".equals(competitionType);
    }

    public boolean isHackathon() {
        return "hackathon".equals(competitionType);
    }

    public boolean isGeneral() {
        return "general".equals(competitionType) || competitionType == null;
    }
}
