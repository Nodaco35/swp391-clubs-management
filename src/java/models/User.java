package models;

public class User {
    private String userID;
    private String fullName;
    private String email;
    private String password;
    private java.sql.Timestamp dateOfBirth;
    private int permissionID;
    private boolean status;
    private String resetToken;
    private java.sql.Timestamp tokenExpiry;

    // Constructors
    public User() {
    }

    public User(String userID, String fullName, String email, String password, int permissionID, boolean status, String resetToken, java.sql.Timestamp tokenExpiry) {
        this.userID = userID;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.permissionID = permissionID;
        this.status = status;
        this.resetToken = resetToken;
        this.tokenExpiry = tokenExpiry;
    }

    public User(String fullName, String email, String password, boolean status, int permissionID) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.status = status;
        this.permissionID = permissionID;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public java.sql.Timestamp getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(java.sql.Timestamp dateOfBirth) {
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

    public java.sql.Timestamp getTokenExpiry() {
        return tokenExpiry;
    }

    public void setTokenExpiry(java.sql.Timestamp tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    }
}
