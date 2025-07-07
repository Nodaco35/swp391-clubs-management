package models;

import java.util.Date;

public class NotificationTemplate {
    private int templateID;
    private int clubID;
    private String templateName;
    private String title;
    private String content;
    private String createdBy;
    private Date createdAt;
    private boolean isReusable;
    
    // Additional fields for UI display
    private String clubName;
    private String createdByName;
    
    public NotificationTemplate() {
    }
    
    public NotificationTemplate(int templateID, int clubID, String templateName, String title, 
                              String content, String createdBy, Date createdAt, boolean isReusable) {
        this.templateID = templateID;
        this.clubID = clubID;
        this.templateName = templateName;
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.isReusable = isReusable;
    }
    
    // Constructor with additional display fields
    public NotificationTemplate(int templateID, int clubID, String templateName, String title, 
                              String content, String createdBy, Date createdAt, boolean isReusable, 
                              String clubName, String createdByName) {
        this.templateID = templateID;
        this.clubID = clubID;
        this.templateName = templateName;
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.isReusable = isReusable;
        this.clubName = clubName;
        this.createdByName = createdByName;
    }

    public int getTemplateID() {
        return templateID;
    }

    public void setTemplateID(int templateID) {
        this.templateID = templateID;
    }

    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
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

    public boolean isReusable() {
        return isReusable;
    }

    public void setReusable(boolean reusable) {
        isReusable = reusable;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public String toString() {
        return "NotificationTemplate{" +
                "templateID=" + templateID +
                ", clubID=" + clubID +
                ", templateName='" + templateName + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                ", isReusable=" + isReusable +
                '}';
    }
}
