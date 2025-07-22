/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author NC PC
 */
public class PeriodicReport_MemberAchievements {

    private int achievementID;
    private int reportID;
    private String memberID;
    private String fullName;      // Join từ Users
    private String studentCode;   // Join từ Users
    private String role;
    private int progressPoint;

    public PeriodicReport_MemberAchievements() {
    }

    public int getAchievementID() {
        return achievementID;
    }

    public void setAchievementID(int achievementID) {
        this.achievementID = achievementID;
    }

    public int getReportID() {
        return reportID;
    }

    public void setReportID(int reportID) {
        this.reportID = reportID;
    }

    public String getMemberID() {
        return memberID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getProgressPoint() {
        return progressPoint;
    }

    public void setProgressPoint(int progressPoint) {
        this.progressPoint = progressPoint;
    }
    
    
}
