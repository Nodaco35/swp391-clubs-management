
package models;

public class EventParticipant {
    private int eventParticipantID;
    private int eventID;
    private int userID;
    private String status; // ENUM('Registered','Attended','Absent','Accepted','Rejected')

    public EventParticipant() {
    }

    public EventParticipant(int eventParticipantID, int eventID, int userID, String status) {
        this.eventParticipantID = eventParticipantID;
        this.eventID = eventID;
        this.userID = userID;
        this.status = status;
    }

    public int getEventParticipantID() {
        return eventParticipantID;
    }

    public void setEventParticipantID(int eventParticipantID) {
        this.eventParticipantID = eventParticipantID;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "EventParticipant{" + "eventParticipantID=" + eventParticipantID + ", eventID=" + eventID + ", userID=" + userID + ", status=" + status + '}';
    }
    
    
}
