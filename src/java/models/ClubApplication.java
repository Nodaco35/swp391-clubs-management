
package models;

import java.sql.Timestamp;

public class ClubApplication {
    private int applicationID;
    private String userID;
    private String clubName;
    private String description;
    private String email;
    private String phone;
    private String status;
    private Timestamp submitDate;

    // Constructors
    public ClubApplication() {}

    public ClubApplication(int applicationID, String userID, String clubName, String description,
                           String email, String phone, String status, Timestamp submitDate) {
        this.applicationID = applicationID;
        this.userID = userID;
        this.clubName = clubName;
        this.description = description;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.submitDate = submitDate;
    }

    public int getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(int applicationID) {
        this.applicationID = applicationID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Timestamp submitDate) {
        this.submitDate = submitDate;
    }

    

}
