
package models;

import java.security.Timestamp;

public class ClubApplication {
    private int applicationID;
    private int userID;
    private int clubID;
    private String reason;
    private String status; // ENUM('Pending','Approved','Rejected')*******
    private Timestamp submitDate;

    public ClubApplication() {
    }

    public ClubApplication(int applicationID, int userID, int clubID, String reason, String status, Timestamp submitDate) {
        this.applicationID = applicationID;
        this.userID = userID;
        this.clubID = clubID;
        this.reason = reason;
        this.status = status;
        this.submitDate = submitDate;
    }

    public int getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(int applicationID) {
        this.applicationID = applicationID;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    @Override
    public String toString() {
        return "ClubApplication{" + "applicationID=" + applicationID + ", userID=" + userID + ", clubID=" + clubID + ", reason=" + reason + ", status=" + status + ", submitDate=" + submitDate + '}';
    }


}
