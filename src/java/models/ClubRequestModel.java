package models;

public class ClubRequestModel {
    // Dùng khi sửa
    private int clubID;

    // Thông tin cơ bản
    private String clubName;
    private String description;
    private Integer categoryID;
    private String establishedDate;  // yyyy-MM-dd
    private String contactPhone;
    private String contactGmail;
    private String contactURL;
    private String clubImg;

    // Trạng thái yêu cầu
    private String requestType; // "Create" hoặc "Update"
    private String lastCommentDescription;

    // Người tạo (có thể gắn qua session)
    private String createdByUserID;

    // Constructors
    public ClubRequestModel() {}

    // Getter & Setter
    public Integer getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }

    public String getEstablishedDate() {
        return establishedDate;
    }

    public void setEstablishedDate(String establishedDate) {
        this.establishedDate = establishedDate;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactGmail() {
        return contactGmail;
    }

    public void setContactGmail(String contactGmail) {
        this.contactGmail = contactGmail;
    }

    public String getContactURL() {
        return contactURL;
    }

    public void setContactURL(String contactURL) {
        this.contactURL = contactURL;
    }

    public String getClubImg() {
        return clubImg;
    }

    public void setClubImg(String clubImg) {
        this.clubImg = clubImg;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getLastCommentDescription() {
        return lastCommentDescription;
    }

    public void setLastCommentDescription(String lastCommentDescription) {
        this.lastCommentDescription = lastCommentDescription;
    }

    public String getCreatedByUserID() {
        return createdByUserID;
    }

    public void setCreatedByUserID(String createdByUserID) {
        this.createdByUserID = createdByUserID;
    }
}

