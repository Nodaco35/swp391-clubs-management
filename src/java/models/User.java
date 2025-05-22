package models;

public class User {
    private String userID;
    private String fullName;
    private String email;
    private String password;
    private int permissionID;
    private boolean status;
    private String resetToken;
    private java.sql.Timestamp tokenExpiry;
    private String dob;

    // Constructors
    public User() {
    }

    public User(String userID, String fullName, String email, String password, int permissionID, boolean status, String resetToken, java.sql.Timestamp tokenExpiry, String dob) {
        this.userID = userID;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.permissionID = permissionID;
        this.status = status;
        this.resetToken = resetToken;
        this.tokenExpiry = tokenExpiry;
        this.dob = dob;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
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

    @Override
    public String toString() {
        return "User{" + "userID=" + userID + ", fullName=" + fullName + ", email=" + email + ", password=" + password + ", permissionID=" + permissionID + ", status=" + status + ", resetToken=" + resetToken + ", tokenExpiry=" + tokenExpiry + '}';
    }


}
