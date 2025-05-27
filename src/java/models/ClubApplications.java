
package models;

import java.util.Date;

public class ClubApplications {
    private int applicationID;
    private String userID;
    private int clubID;
    private String email;
    private String status; // ENUM('PENDING', 'CANDIDATE', 'COLLABORATOR', 'APPROVED', 'REJECTED')
    private Date submitDate;

    public ClubApplications() {}

    public ClubApplications(int applicationID, String userID, int clubID, String email, String status, Date submitDate) {
        this.applicationID = applicationID;
        this.userID = userID;
        this.clubID = clubID;
        this.email = email;
        this.status = status;
        this.submitDate = submitDate;
    }

    public int getApplicationID() { return applicationID; }
    public void setApplicationID(int applicationID) { this.applicationID = applicationID; }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public int getClubID() { return clubID; }
    public void setClubID(int clubID) { this.clubID = clubID; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getSubmitDate() { return submitDate; }
    public void setSubmitDate(Date submitDate) { this.submitDate = submitDate; }
}
