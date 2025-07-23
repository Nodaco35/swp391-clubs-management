package models;

public class ActivedMemberClubs {
    private int activeID;
    private String userID;
    private int clubID;
    private String termID;
    private String role;
    private String department;
    private boolean isActive;
    private Integer progressPoint;

    // Thông tin mở rộng để in view
    private String fullName;
    private String studentCode;

    public ActivedMemberClubs() {}

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getActiveID() {
        return activeID;
    }

    public void setActiveID(int activeID) {
        this.activeID = activeID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }

    public String getTermID() {
        return termID;
    }

    public void setTermID(String termID) {
        this.termID = termID;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Integer getProgressPoint() {
        return progressPoint;
    }

    public void setProgressPoint(Integer progressPoint) {
        this.progressPoint = progressPoint;
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

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    
}
