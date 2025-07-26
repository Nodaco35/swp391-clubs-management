package models;

import java.sql.Timestamp;
import java.sql.Date;
import com.google.gson.annotations.SerializedName;

public class DepartmentMember {
    private String userID;
    private String fullName;
    private String email;
    private String phone;
    private String avatar;
    private String roleName;
    private Timestamp joinedDate;
    
    @SerializedName("active") // Đảm bảo JSON sử dụng "active" thay vì "isActive"
    private boolean isActive;
    
    private String status;
    private Date dateOfBirth; // Added field
    private String gen; // Added field
      // Department info
    private int clubDepartmentID;  // Khóa chính - ID của ban trong CLB cụ thể
    private int departmentID;      // Reference - ID loại ban (để hiển thị)
    private String departmentName;
    private int clubID;
    private String clubName;
    
    // Additional info
    private String studentCode;
    private String major;
    private int completedTasks;
    private int assignedTasks;
    private Timestamp lastActivity;

    public DepartmentMember() {
    }

    public DepartmentMember(String userID, String fullName, String email, String roleName, boolean isActive) {
        this.userID = userID;
        this.fullName = fullName;
        this.email = email;
        this.roleName = roleName;
        this.isActive = isActive;
    }

    // Getters and Setters
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Timestamp getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(Timestamp joinedDate) {
        this.joinedDate = joinedDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }

    public int getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(int assignedTasks) {
        this.assignedTasks = assignedTasks;
    }

    public Timestamp getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Timestamp lastActivity) {
        this.lastActivity = lastActivity;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGen() {
        return gen;
    }

    public void setGen(String gen) {
        this.gen = gen;
    }

    @Override
    public String toString() {
        return "DepartmentMember{" +
                "userID=" + userID +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", roleName='" + roleName + '\'' +
                ", isActive=" + isActive +
                ", departmentName='" + departmentName + '\'' +
                '}';
    }
}
