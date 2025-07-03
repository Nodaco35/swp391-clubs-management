package models;

import java.util.Date;

public class RecruitmentCampaign {
    private int recruitmentID;
    private int clubID;
    private int gen;
    private int templateID;
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private String status; // UPCOMING, ONGOING, CLOSED
    private String createdBy;
    private Date createdAt;
    
    // Additional fields for UI display (not in database)
    private String clubName;
    private String templateName;
    private String createdByName;
    
    public RecruitmentCampaign() {
    }

    public RecruitmentCampaign(int recruitmentID, int clubID, int gen, int templateID, String title, 
                              String description, Date startDate, Date endDate, String status, 
                              String createdBy, Date createdAt) {
        this.recruitmentID = recruitmentID;
        this.clubID = clubID;
        this.gen = gen;
        this.templateID = templateID;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    // Constructor with additional display fields
    public RecruitmentCampaign(int recruitmentID, int clubID, int gen, int templateID, String title, 
                              String description, Date startDate, Date endDate, String status, 
                              String createdBy, Date createdAt, String clubName, String templateName, 
                              String createdByName) {
        this.recruitmentID = recruitmentID;
        this.clubID = clubID;
        this.gen = gen;
        this.templateID = templateID;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.clubName = clubName;
        this.templateName = templateName;
        this.createdByName = createdByName;
    }

    public int getRecruitmentID() {
        return recruitmentID;
    }

    public void setRecruitmentID(int recruitmentID) {
        this.recruitmentID = recruitmentID;
    }

    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }

    public int getGen() {
        return gen;
    }

    public void setGen(int gen) {
        this.gen = gen;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
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
        return "RecruitmentCampaign{" +
                "recruitmentID=" + recruitmentID +
                ", clubID=" + clubID +
                ", gen=" + gen +
                ", templateID=" + templateID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                ", clubName='" + clubName + '\'' +
                ", templateName='" + templateName + '\'' +
                ", createdByName='" + createdByName + '\'' +
                '}';
    }
}
