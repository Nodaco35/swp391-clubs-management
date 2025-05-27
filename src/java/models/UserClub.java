
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
