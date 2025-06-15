
package models;

import java.util.Date;

public class Events {
    private int eventID;
    private String eventName;
    private String eventImg;
    private String description;

    private Date eventDate;
    private String location;
    private int clubID;
    private boolean isPublic;
    private int formTemplateID;

    private int capacity;
    private String status;
    
    // Thêm các trường bổ sung cho hiển thị
    private String clubName;
    private String clubImg;
    
    //Them de tinh so nguoi da tham gia su kien
    private int registered;
    private int spotsLeft;


    public Events() {
    }

    public Events(int eventID, String eventName, String eventImg, String description, Date eventDate, String location, int clubID, boolean isPublic, int formTemplateID, int capacity, String status, String clubName, String clubImg) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventImg = eventImg;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.clubID = clubID;
        this.isPublic = isPublic;
        this.formTemplateID = formTemplateID;
        this.capacity = capacity;
        this.status = status;
        this.clubName = clubName;
        this.clubImg = clubImg;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventImg() {
        return eventImg;
    }

    public void setEventImg(String eventImg) {
        this.eventImg = eventImg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public int getFormTemplateID() {
        return formTemplateID;
    }

    public void setFormTemplateID(int formTemplateID) {
        this.formTemplateID = formTemplateID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubImg() {
        return clubImg;
    }
    
    public void setClubImg(String clubImg) {
        this.clubImg = clubImg;
    }

    public int getRegistered() {
        return registered;
    }

    public void setRegistered(int registered) {
        this.registered = registered;
    }

    public int getSpotsLeft() {
        return spotsLeft;
    }

    public void setSpotsLeft(int spotsLeft) {
        this.spotsLeft = spotsLeft;
    }

}
