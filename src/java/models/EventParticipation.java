package models;

import java.util.Date;
import java.util.List;

public class EventParticipation {

    private String eventID;
    private String eventName;
    private String clubName;
    private String participationStatus;
    private String eventStatus;

    private List<EventScheduleDetail> scheduleList;

    public List<EventScheduleDetail> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<EventScheduleDetail> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public EventParticipation() {
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getParticipationStatus() {
        return participationStatus;
    }

    public void setParticipationStatus(String participationStatus) {
        this.participationStatus = participationStatus;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }
}
