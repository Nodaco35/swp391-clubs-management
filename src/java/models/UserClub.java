package models;

import java.util.Date;

public class UserClub {
    private int userClubID;
    private String userID;
    private int clubID;
    private int departmentID;
    private int roleID;
    private Date joinDate;
    private boolean isActive;
    private String fullName; // Thêm trường fullName
    private String roleName; // Thêm trường roleName
    private String departmentName; // Thêm trường departmentName
    private String ClubImg;
    private String ClubName;
            
    public UserClub() {
    }

    public UserClub(int userClubID, String userID, int clubID, int departmentID, int roleID, Date joinDate, boolean isActive) {
        this.userClubID = userClubID;
        this.userID = userID;
        this.clubID = clubID;
        this.departmentID = departmentID;
        this.roleID = roleID;
        this.joinDate = joinDate;
        this.isActive = isActive;
    }

    public String getClubName() {
        return ClubName;
    }

    public void setClubName(String ClubName) {
        this.ClubName = ClubName;
    }

    
    public String getClubImg() {
        return ClubImg;
    }

    public void setClubImg(String ClubImg) {
        this.ClubImg = ClubImg;
    }
    

    public int getUserClubID() {
        return userClubID;
    }

    public void setUserClubID(int userClubID) {
        this.userClubID = userClubID;
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

    public int getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        this.roleID = roleID;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public String toString() {
        return "UserClub{" + "userClubID=" + userClubID + ", userID=" + userID + ", clubID=" + clubID + ", departmentID=" + departmentID + ", roleID=" + roleID + ", joinDate=" + joinDate + ", isActive=" + isActive + ", fullName=" + fullName + ", roleName=" + roleName + ", departmentName=" + departmentName + ", ClubImg=" + ClubImg + ", ClubName=" + ClubName + '}';
    }
    
    

   
    
}