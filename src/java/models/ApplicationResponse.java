package models;

import java.util.Date;

public class ApplicationResponse {
    private int responseID;
    private int templateID;
    private String userID;
    private int clubID;
    private Integer eventID; // Có thể null
    private String responses; // JSON format
    private String status;
    private Date submitDate;
    
    // Constructors
    public ApplicationResponse() {
    }
    
    public ApplicationResponse(int templateID, String userID, int clubID, 
                              Integer eventID, String responses, String status) {
        this.templateID = templateID;
        this.userID = userID;
        this.clubID = clubID;
        this.eventID = eventID;
        this.responses = responses;
        this.status = status;
        this.submitDate = new Date(); // Thời gian hiện tại
    }
    
    // Getters and Setters
    public int getResponseID() {
        return responseID;
    }

    public void setResponseID(int responseID) {
        this.responseID = responseID;
    }

    public int getTemplateID() {
        return templateID;
    }

    public void setTemplateID(int templateID) {
        this.templateID = templateID;
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