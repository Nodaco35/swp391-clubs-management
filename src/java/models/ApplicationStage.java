package models;

import java.util.Date;

public class ApplicationStage {
    private int applicationStageID;
    private int applicationID;
    private int stageID;
    private String status; // PENDING, PASSED, FAILED
    private Date statusDate;
    private String notes;
    private String updatedBy;
    
    // Additional fields for UI display
    private String stageName;
    private String applicationUserName;
    private String applicationEmail;
    private String updatedByName;
    
    public ApplicationStage() {
    }
    
    public ApplicationStage(int applicationStageID, int applicationID, int stageID, String status, 
                          Date statusDate, String notes, String updatedBy) {
        this.applicationStageID = applicationStageID;
        this.applicationID = applicationID;
        this.stageID = stageID;
        this.status = status;
        this.statusDate = statusDate;
        this.notes = notes;
        this.updatedBy = updatedBy;
    }
    
    // Constructor with additional display fields
    public ApplicationStage(int applicationStageID, int applicationID, int stageID, String status, 
                          Date statusDate, String notes, String updatedBy, String stageName,
                          String applicationUserName, String applicationEmail, String updatedByName) {
        this.applicationStageID = applicationStageID;
        this.applicationID = applicationID;
        this.stageID = stageID;
        this.status = status;
        this.statusDate = statusDate;
        this.notes = notes;
        this.updatedBy = updatedBy;
        this.stageName = stageName;
        this.applicationUserName = applicationUserName;
        this.applicationEmail = applicationEmail;
        this.updatedByName = updatedByName;
    }

    public int getApplicationStageID() {
        return applicationStageID;
    }

    public void setApplicationStageID(int applicationStageID) {
        this.applicationStageID = applicationStageID;
    }

    public int getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(int applicationID) {
        this.applicationID = applicationID;
    }

    public int getStageID() {
        return stageID;
    }

    public void setStageID(int stageID) {
        this.stageID = stageID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getApplicationUserName() {
        return applicationUserName;
    }

    public void setApplicationUserName(String applicationUserName) {
        this.applicationUserName = applicationUserName;
    }

    public String getApplicationEmail() {
        return applicationEmail;
    }

    public void setApplicationEmail(String applicationEmail) {
        this.applicationEmail = applicationEmail;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }

    @Override
    public String toString() {
        return "ApplicationStage{" +
                "applicationStageID=" + applicationStageID +
                ", applicationID=" + applicationID +
                ", stageID=" + stageID +
                ", status='" + status + '\'' +
                ", statusDate=" + statusDate +
                ", notes='" + notes + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}
