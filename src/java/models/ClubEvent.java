package models;

import java.util.Date;
import java.util.List;

public class ClubEvent {

    private int eventID;
    private String eventName;
    private int participantCount;
    private List<EventScheduleDetail> scheduleList;

    public List<EventScheduleDetail> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<EventScheduleDetail> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public ClubEvent() {
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

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }
}
