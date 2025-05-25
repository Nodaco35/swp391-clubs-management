
package models;

public class EventParticipants {
    private int eventParticipantID;
    private int eventID;
    private String userID;
    private String status; // ENUM('REGISTERED', 'ATTENDED', 'ABSENT')

    public EventParticipants() {}

    public EventParticipants(int eventParticipantID, int eventID, String userID, String status) {
        this.eventParticipantID = eventParticipantID;
        this.eventID = eventID;
        this.userID = userID;
        this.status = status;
    }

    public int getEventParticipantID() { return eventParticipantID; }
    public void setEventParticipantID(int eventParticipantID) { this.eventParticipantID = eventParticipantID; }

    public int getEventID() { return eventID; }
    public void setEventID(int eventID) { this.eventID = eventID; }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
