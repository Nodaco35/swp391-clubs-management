package models;

import java.util.Date;

public class RecruitmentStage {
    private int stageID;
    private int recruitmentID;
    private String stageName;
    private String description;
    private Date startDate;
    private Date endDate;
    private String status; // UPCOMING, ONGOING, COMPLETED
    private int locationID;
    private String createdBy;
    private Date createdAt;
    
    // Additional fields for UI display
    private String locationName;
    private String locationAddress;
    private String recruitmentTitle;
    private String createdByName;
    
    // Additional properties for JSON serialization (camelCase)
    private int stageId;
    private int locationId;
    
    public RecruitmentStage() {
    }
    
    public RecruitmentStage(int stageID, int recruitmentID, String stageName, String description, 
                           Date startDate, Date endDate, String status, int locationID, 
                           String createdBy, Date createdAt) {
        this.stageID = stageID;
        this.recruitmentID = recruitmentID;
        this.stageName = stageName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.locationID = locationID;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
    
    // Constructor with additional display fields
    public RecruitmentStage(int stageID, int recruitmentID, String stageName, String description, 
                           Date startDate, Date endDate, String status, int locationID, 
                           String createdBy, Date createdAt, String locationName, String locationAddress,
                           String recruitmentTitle, String createdByName) {
        this.stageID = stageID;
        this.recruitmentID = recruitmentID;
        this.stageName = stageName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.locationID = locationID;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.locationName = locationName;
        this.locationAddress = locationAddress;
        this.recruitmentTitle = recruitmentTitle;
        this.createdByName = createdByName;
    }

    public int getStageID() {
        return stageID;
    }

    public void setStageID(int stageID) {
        this.stageID = stageID;
    }

    public int getRecruitmentID() {
        return recruitmentID;
    }

    public void setRecruitmentID(int recruitmentID) {
        this.recruitmentID = recruitmentID;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
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

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
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

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getRecruitmentTitle() {
        return recruitmentTitle;
    }

    public void setRecruitmentTitle(String recruitmentTitle) {
        this.recruitmentTitle = recruitmentTitle;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public int getStageId() {
        return stageId > 0 ? stageId : stageID;
    }
    
    public void setStageId(int stageId) {
        this.stageId = stageId;
    }
    
    public int getLocationId() {
        return locationId > 0 ? locationId : locationID;
    }
    
    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    /**
     * Lấy địa điểm dưới dạng chuỗi (tên hoặc địa chỉ nếu có)
     * @return Địa điểm của vòng tuyển
     */
    public String getLocation() {
        if (locationName != null && !locationName.trim().isEmpty()) {
            if (locationAddress != null && !locationAddress.trim().isEmpty()) {
                return locationName + " - " + locationAddress;
            }
            return locationName;
        }
        if (locationAddress != null && !locationAddress.trim().isEmpty()) {
            return locationAddress;
        }
        return null;
    }

    @Override
    public String toString() {
        return "RecruitmentStage{" +
                "stageID=" + stageID +
                ", recruitmentID=" + recruitmentID +
                ", stageName='" + stageName + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                ", locationID=" + locationID +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
