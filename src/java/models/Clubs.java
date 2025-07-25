package models;

import java.util.Date;

public class Clubs {
    private int clubID;
    private String clubImg;
    private boolean isRecruiting;
    private String clubName;
    private String description;
    private Date establishedDate;
    private String contactPhone;
    private String contactGmail;
    private String contactURL;
    private boolean clubStatus;
    private int categoryID;
    private String categoryName;
    private int memberCount;
    private boolean isFavorite;
    
    //Dùng cho xin cấp phép tạo/sửa
    private String clubRequestStatus;
    private String currentRequestType;
    private int parentClubID;
    private String lastRejectReason;
    private String updateRequestNote;
    private String chairmanID;
    private String chairmanFullName;

    public Clubs() {
    }

    public Clubs(int clubID, String clubImg, boolean isRecruiting, String clubName, String description,
                 Date establishedDate, String contactPhone, String contactGmail, String contactURL,
                 boolean clubStatus, int categoryID, String categoryName, int memberCount, boolean isFavorite) {
        this.clubID = clubID;
        this.clubImg = clubImg;
        this.isRecruiting = isRecruiting;
        this.clubName = clubName;
        this.description = description;
        this.establishedDate = establishedDate;
        this.contactPhone = contactPhone;
        this.contactGmail = contactGmail;
        this.contactURL = contactURL;
        this.clubStatus = clubStatus;
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.memberCount = memberCount;
        this.isFavorite = isFavorite;
    }

    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }

    public String getClubImg() {
        return clubImg;
    }

    public void setClubImg(String clubImg) {
        this.clubImg = clubImg;
    }

    public boolean isIsRecruiting() {
        return isRecruiting;
    }

    public void setIsRecruiting(boolean isRecruiting) {
        this.isRecruiting = isRecruiting;
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

    public Date getEstablishedDate() {
        return establishedDate;
    }

    public void setEstablishedDate(Date establishedDate) {
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

    public boolean isClubStatus() {
        return clubStatus;
    }

    public void setClubStatus(boolean clubStatus) {
        this.clubStatus = clubStatus;
    }
    
    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public boolean isIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public String getClubRequestStatus() {
        return clubRequestStatus;
    }

    public void setClubRequestStatus(String ClubRequestStatus) {
        this.clubRequestStatus = ClubRequestStatus;
    }

    public String getCurrentRequestType() {
        return currentRequestType;
    }

    public void setCurrentRequestType(String CurrentRequestType) {
        this.currentRequestType = CurrentRequestType;
    }

    public int getParentClubID() {
        return parentClubID;
    }

    public void setParentClubID(int ParentClubID) {
        this.parentClubID = ParentClubID;
    }

    public String getLastRejectReason() {
        return lastRejectReason;
    }

    public void setLastRejectReason(String LastRejectReason) {
        this.lastRejectReason = LastRejectReason;
    }

    public String getUpdateRequestNote() {
        return updateRequestNote;
    }

    public void setUpdateRequestNote(String UpdateRequestNote) {
        this.updateRequestNote = UpdateRequestNote;
    }

    public String getChairmanID() {
        return chairmanID;
    }

    public void setChairmanID(String ChairmanID) {
        this.chairmanID = ChairmanID;
    }

    public String getChairmanFullName() {
        return chairmanFullName;
    }

    public void setChairmanFullName(String chairmanFullName) {
        this.chairmanFullName = chairmanFullName;
    }

    @Override
    public String toString() {
        return "Clubs{" + "clubID=" + clubID + ", clubImg=" + clubImg + ", isRecruiting=" + isRecruiting + ", clubName=" + clubName + ", description=" + description + ", establishedDate=" + establishedDate + ", contactPhone=" + contactPhone + ", contactGmail=" + contactGmail + ", contactURL=" + contactURL + ", clubStatus=" + clubStatus + ", categoryID=" + categoryID + ", categoryName=" + categoryName + ", memberCount=" + memberCount + ", isFavorite=" + isFavorite + ", clubRequestStatus=" + clubRequestStatus + ", currentRequestType=" + currentRequestType + ", parentClubID=" + parentClubID + ", lastRejectReason=" + lastRejectReason + ", updateRequestNote=" + updateRequestNote + ", chairmanID=" + chairmanID + ", chairmanFullName=" + chairmanFullName + '}';
    }
    
    
}