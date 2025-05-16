
package models;

import java.security.Timestamp;

public class Event {
    private int eventID;
    private String eventName;
    private String description;
    private Timestamp eventDate;
    private String location;
    private int clubID;
    private boolean isPublic;
    private String urlGGForm;

    public Event() {
    }

    public Event(int eventID, String eventName, String description, Timestamp eventDate, String location, int clubID, boolean isPublic, String urlGGForm) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.clubID = clubID;
        this.isPublic = isPublic;
        this.urlGGForm = urlGGForm;
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

    public boolean isIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getUrlGGForm() {
        return urlGGForm;
    }

    public void setUrlGGForm(String urlGGForm) {
        this.urlGGForm = urlGGForm;
    }

    @Override
    public String toString() {
        return "Event{" + "eventID=" + eventID + ", eventName=" + eventName + ", description=" + description + ", eventDate=" + eventDate + ", location=" + location + ", clubID=" + clubID + ", isPublic=" + isPublic + ", urlGGForm=" + urlGGForm + '}';
    }
    
    
}
