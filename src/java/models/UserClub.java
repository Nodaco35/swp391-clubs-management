
package models;

import java.util.Date;


public class UserClub {
    private int userClubID;
    private int userID;
    private int clubID;
    private int departmentID;
    private int roleID;
    private Date joinDate;
    private boolean isActive;

    public UserClub() {
    }

    public UserClub(int userClubID, int userID, int clubID, int departmentID, int roleID, Date joinDate, boolean isActive) {
        this.userClubID = userClubID;
        this.userID = userID;
        this.clubID = clubID;
        this.departmentID = departmentID;
        this.roleID = roleID;
        this.joinDate = joinDate;
        this.isActive = isActive;
    }

    public int getUserClubID() {
        return userClubID;
    }

    public void setUserClubID(int userClubID) {
        this.userClubID = userClubID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
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

    @Override
    public String toString() {
        return "UserClub{" + "userClubID=" + userClubID + ", userID=" + userID + ", clubID=" + clubID + ", departmentID=" + departmentID + ", roleID=" + roleID + ", joinDate=" + joinDate + ", isActive=" + isActive + '}';
    }
    
    
}
