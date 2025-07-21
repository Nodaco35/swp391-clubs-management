package models;

import java.util.Date;

public class ApplicationResponse {
    private int responseID;
    private Integer formID;
    private String userID;
    private int clubID;
    private Integer eventID;
    private String responses; // JSON format
    private String status;
    private Date submitDate;
    
    // Constructors
    public ApplicationResponse() {
    }

    public ApplicationResponse(int responseID, int formID, String userID, int clubID, Integer eventID, String responses, String status, Date submitDate) {
        this.responseID = responseID;
        this.formID = formID;
        this.userID = userID;
        this.clubID = clubID;
        this.eventID = eventID;
        this.responses = responses;
        this.status = status;
        this.submitDate = submitDate;
    }
    
    // Getters and Setters
    public int getResponseID() {
        return responseID;
    }

    public void setResponseID(int responseID) {
        this.responseID = responseID;
    }

    public Integer getFormID() {
        return formID;
    }

    public void setFormID(Integer formID) {
        this.formID = formID;
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

    public Integer getEventID() {
        return eventID;
    }

    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    public String getResponses() {
        return responses;
    }

    public void setResponses(String responses) {
        this.responses = responses;
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

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }
}