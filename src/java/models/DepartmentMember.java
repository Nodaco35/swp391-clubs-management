package models;

import java.util.Date;

/**
 * Model class for representing a member of a department
 * Note: This is a view model that may combine data from UserClub, ClubDepartment, and User tables
 * It is used to simplify working with department members in the Department Leader module
 */
public class DepartmentMember {
    private int userClubID;
    private String userID;
    private int clubID;
    private int clubDepartmentID;
    private int departmentID;
    private int roleID;
    private String roleName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Date joinDate;
    private boolean isActive;
    private String departmentName;
    private String avatarUrl;

    public DepartmentMember() {
    }

    public DepartmentMember(int userClubID, String userID, int clubID, int clubDepartmentID, int departmentID, 
                           int roleID, String roleName, String fullName, String email, String phoneNumber,
                           Date joinDate, boolean isActive, String departmentName, String avatarUrl) {
        this.userClubID = userClubID;
        this.userID = userID;
        this.clubID = clubID;
        this.clubDepartmentID = clubDepartmentID;
        this.departmentID = departmentID;
        this.roleID = roleID;
        this.roleName = roleName;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.joinDate = joinDate;
        this.isActive = isActive;
        this.departmentName = departmentName;
        this.avatarUrl = avatarUrl;
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

    public int getClubDepartmentID() {
        return clubDepartmentID;
    }

    public void setClubDepartmentID(int clubDepartmentID) {
        this.clubDepartmentID = clubDepartmentID;
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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "DepartmentMember{" +
                "userClubID=" + userClubID +
                ", userID='" + userID + '\'' +
                ", clubID=" + clubID +
                ", clubDepartmentID=" + clubDepartmentID +
                ", departmentID=" + departmentID +
                ", roleID=" + roleID +
                ", roleName='" + roleName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", isActive=" + isActive +
                ", departmentName='" + departmentName + '\'' +
                '}';
    }
}
