package models;

import java.util.Date;

public class StageNotification {
    private int notificationID;
    private int stageID;
    private int templateID; // Reference to NotificationTemplate if using a reusable template
    private String title;
    private String content;
    private boolean isSent;
    private Date scheduledDate;
    private Date sentDate;
    private String createdBy;
    private Date createdAt;
    
    // Additional fields for UI display
    private String stageName;
    private String templateName;
    private String createdByName;
    
    public StageNotification() {
    }
    
    public StageNotification(int notificationID, int stageID, int templateID, String title, 
                           String content, boolean isSent, Date scheduledDate, Date sentDate, 
                           String createdBy, Date createdAt) {
        this.notificationID = notificationID;
        this.stageID = stageID;
        this.templateID = templateID;
        this.title = title;
        this.content = content;
        this.isSent = isSent;
        this.scheduledDate = scheduledDate;
        this.sentDate = sentDate;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
    
    // Constructor with additional display fields
    public StageNotification(int notificationID, int stageID, int templateID, String title, 
                           String content, boolean isSent, Date scheduledDate, Date sentDate, 
                           String createdBy, Date createdAt, String stageName, String templateName, 
                           String createdByName) {
        this.notificationID = notificationID;
        this.stageID = stageID;
        this.templateID = templateID;
        this.title = title;
        this.content = content;
        this.isSent = isSent;
        this.scheduledDate = scheduledDate;
        this.sentDate = sentDate;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.stageName = stageName;
        this.templateName = templateName;
        this.createdByName = createdByName;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public int getStageID() {
        return stageID;
    }

    public void setStageID(int stageID) {
        this.stageID = stageID;
    }

    public int getTemplateID() {
        return templateID;
    }

    public void setTemplateID(int templateID) {
        this.templateID = templateID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isIsSent() {
        return isSent;
    }

    public void setIsSent(boolean isSent) {
        this.isSent = isSent;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public String toString() {
        return "StageNotification{" +
                "notificationID=" + notificationID +
                ", stageID=" + stageID +
                ", templateID=" + templateID +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", isSent=" + isSent +
                ", scheduledDate=" + scheduledDate +
                ", sentDate=" + sentDate +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
