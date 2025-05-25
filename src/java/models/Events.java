package models;


import java.sql.Timestamp;

public class Events {
    private int eventID;
    private String eventName;
    private String description;
    private Timestamp eventDate;
    private String location;
    private int clubID;
    private boolean isPublic;
    private String urlGGForm;
    private int capacity;
    private String status; // ENUM('PENDING', 'COMPLETED')

    public Events() {
    }

    public Events(int eventID, String eventName, String description, Timestamp eventDate, String location, int clubID, boolean isPublic, String urlGGForm, int capacity, String status) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.clubID = clubID;
        this.isPublic = isPublic;
        this.urlGGForm = urlGGForm;
        this.capacity = capacity;
        this.status = status;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getEventDate() {
        return eventDate;
    }

    public void setEventDate(Timestamp eventDate) {
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

    public String getUrlGGForm() {
        return urlGGForm;
    }

    public void setUrlGGForm(String urlGGForm) {
        this.urlGGForm = urlGGForm;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
