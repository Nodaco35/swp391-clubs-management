package models;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Model class for ClubApplications table
 */
public class ClubApplication {
    private int applicationId;
    private String userId;
    private int clubId;
    private String email;
    private Integer eventId;        // nullable
    private int responseId;
    private String status;
    private Timestamp submitDate;
    private int clubID;
    private String userName;
    private String clubName;

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }
    
    


    public ClubApplication() {
        // default constructor
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    
    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }
    
    public int getApplicationID() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public int getResponseId() {
        return responseId;
    }

    public void setResponseId(int responseId) {
        this.responseId = responseId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Timestamp submitDate) {
        this.submitDate = submitDate;
    }

    @Override
    public String toString() {

        return "ClubApplication{" +
                "applicationId=" + applicationId +
                ", userId='" + userId + '\'' +
                ", clubId=" + clubId +
                ", email='" + email + '\'' +
                ", eventId=" + eventId +
                ", responseId=" + responseId +
                ", status='" + status + '\'' +
                ", submitDate=" + submitDate +
                '}';
    }

}
