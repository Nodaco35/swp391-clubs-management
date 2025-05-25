package models;

import java.sql.Date;
import java.sql.Timestamp;

public class User {
    private String userID;
    private String fullName;
    private String email;
    private String password;
    private Date dateOfBirth;
    private int permissionID;
    private boolean status;
    private String resetToken;
    private java.sql.Timestamp tokenExpiry;

    // Constructors
    public User() {
    }

    public User(String userID, String fullName, String email, String password, Date dateOfBirth, int permissionID, boolean status, String resetToken, Timestamp tokenExpiry) {
        this.userID = userID;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.permissionID = permissionID;
        this.status = status;
        this.resetToken = resetToken;
        this.tokenExpiry = tokenExpiry;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getPermissionID() {
        return permissionID;
    }

    public void setPermissionID(int permissionID) {
        this.permissionID = permissionID;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public Timestamp getTokenExpiry() {
        return tokenExpiry;
    }

    public void setTokenExpiry(Timestamp tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", permissionID=" + permissionID +
                ", status=" + status +
                ", resetToken='" + resetToken + '\'' +
                ", tokenExpiry=" + tokenExpiry +
                '}';
    }
}
