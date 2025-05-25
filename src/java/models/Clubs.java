/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.util.Date;

/**
 *
 * @author NC PC
 */

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

    public Clubs() {}

    public Clubs(int clubID, String clubImg, boolean isRecruiting, String clubName, String description, Date establishedDate, String contactPhone, String contactGmail, String contactURL, boolean clubStatus) {
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
    }

    public int getClubID() { return clubID; }
    public void setClubID(int clubID) { this.clubID = clubID; }

    public String getClubImg() { return clubImg; }
    public void setClubImg(String clubImg) { this.clubImg = clubImg; }

    public boolean isRecruiting() { return isRecruiting; }
    public void setRecruiting(boolean recruiting) { isRecruiting = recruiting; }

    public String getClubName() { return clubName; }
    public void setClubName(String clubName) { this.clubName = clubName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getEstablishedDate() { return establishedDate; }
    public void setEstablishedDate(Date establishedDate) { this.establishedDate = establishedDate; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactGmail() { return contactGmail; }
    public void setContactGmail(String contactGmail) { this.contactGmail = contactGmail; }

    public String getContactURL() { return contactURL; }
    public void setContactURL(String contactURL) { this.contactURL = contactURL; }

    public boolean isClubStatus() { return clubStatus; }
    public void setClubStatus(boolean clubStatus) { this.clubStatus = clubStatus; }
}

