package models;

import java.util.Date;

public class ActivedMembers {
    private String userID;
    private String fullName;
    private String email;
    private int clubID;
    private Date activeDate;
    private Date leaveDate;
    private int progressPoint;
    
    private int taskcount;
    
    private int positive;
    private int neutral;
    private int negative;

    public ActivedMembers() {
    }

    public ActivedMembers(String userID, String fullName, String email, int clubID, Date activeDate, Date leaveDate, int progressPoint) {
        this.userID = userID;
        this.fullName = fullName;
        this.email = email;
        this.clubID = clubID;
        this.activeDate = activeDate;
        this.leaveDate = leaveDate;
        this.progressPoint = progressPoint;
    }

    public int getTaskcount() {
        return taskcount;
    }

    public void setTaskcount(int taskcount) {
        this.taskcount = taskcount;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }

    public Date getActiveDate() {
        return activeDate;
    }

    public void setActiveDate(Date activeDate) {
        this.activeDate = activeDate;
    }

    public Date getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }

    public int getProgressPoint() {
        return progressPoint;
    }

    public void setProgressPoint(int progressPoint) {
        this.progressPoint = progressPoint;
    }

    public int getPositive() {
        return positive;
    }

    public void setPositive(int positive) {
        this.positive = positive;
    }

    public int getNeutral() {
        return neutral;
    }

    public void setNeutral(int neutral) {
        this.neutral = neutral;
    }

    public int getNegative() {
        return negative;
    }

    public void setNegative(int negative) {
        this.negative = negative;
    }
    
    @Override
    public String toString() {
        return "ActivedMembers{" + "userID=" + userID + ", fullName=" + fullName + ", email=" + email + ", clubID=" + clubID + ", activeDate=" + activeDate + ", leaveDate=" + leaveDate + ", progressPoint=" + progressPoint + '}';
    }

    
    
    
}

