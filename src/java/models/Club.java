
package models;


import java.util.Date;


public class Club {
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
    private String category; // Thêm trường category
    private int memberCount; // Thêm trường memberCount
    
    public Club() {
    }
    
    public Club(int clubID, String clubImg, boolean isRecruiting, String clubName, String description, 
                Date establishedDate, String contactPhone, String contactGmail, String contactURL, 
                boolean clubStatus, String category, int memberCount) {
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
        this.category = category;
        this.memberCount = memberCount;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

}