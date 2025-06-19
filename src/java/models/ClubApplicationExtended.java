package models;

import java.sql.Timestamp;

/**
 * Extended model class for ClubApplications that includes additional fields from joins
 * Used to merge data from ClubApplications, ApplicationResponses, Users, and ApplicationFormTemplates
 */
public class ClubApplicationExtended extends ClubApplication {
    private String fullName;  // Lấy từ Users dựa trên userId
    private String responses; // JSON từ ApplicationResponses
    private String responseStatus; // Trạng thái từ ApplicationResponses
    private int templateId;   // ID của form template
    private String formType;  // Loại form (member/event)
    
    public ClubApplicationExtended() {
        super();
    }
    
    // Constructor tạo từ ClubApplication
    public ClubApplicationExtended(ClubApplication app) {
        super();
        this.setApplicationId(app.getApplicationID());
        this.setUserId(app.getUserId());
        this.setClubId(app.getClubId());
        this.setEmail(app.getEmail());
        this.setEventId(app.getEventId());
        this.setResponseId(app.getResponseId());
        this.setStatus(app.getStatus());
        this.setSubmitDate((Timestamp) app.getSubmitDate());
    }
    
    // Getters and setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getResponses() {
        return responses;
    }

    public void setResponses(String responses) {
        this.responses = responses;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }
    
    @Override
    public String toString() {
        return "ClubApplicationExtended{" +
                "applicationId=" + getApplicationID()+
                ", userId='" + getUserId() + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + getEmail() + '\'' +
                ", clubId=" + getClubId() +
                ", eventId=" + getEventId() +
                ", responseId=" + getResponseId() +
                ", status='" + getStatus() + '\'' +
                ", responseStatus='" + responseStatus + '\'' +
                ", templateId=" + templateId +
                ", formType='" + formType + '\'' +
                ", submitDate=" + getSubmitDate() +
                '}';
    }
}
