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
public class Club {
    private int clubID;
    private String clubName;
    private String description;
    private Date establishedDate;
    private String contactPhone;
    private String contactGmail;
    private String contactURL;

    public Club() {
    }

    public Club(int clubID, String clubName, String description, Date establishedDate, String contactPhone, String contactGmail, String contactURL) {
        this.clubID = clubID;
        this.clubName = clubName;
        this.description = description;
        this.establishedDate = establishedDate;
        this.contactPhone = contactPhone;
        this.contactGmail = contactGmail;
        this.contactURL = contactURL;
    }

    public int getClubID() {
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

    @Override
    public String toString() {
        return "Club{" + "clubID=" + clubID + ", clubName=" + clubName + ", description=" + description + ", establishedDate=" + establishedDate + ", contactPhone=" + contactPhone + ", contactGmail=" + contactGmail + ", contactURL=" + contactURL + '}';
    }
    
    
}
