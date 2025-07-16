package models;

import java.sql.Timestamp;

public class Agenda {

    private int agendaID;
    private int eventID;
    private String title;
    private String description;
    private String status;
    private String reason;
    private Timestamp startTime;
    private Timestamp endTime;
    private Events event;
    public Agenda() {
    }

    public Agenda(int agendaID, Timestamp endTime, Timestamp startTime, String title, String description, int eventID) {
        this.agendaID = agendaID;
        this.endTime = endTime;
        this.startTime = startTime;
        this.title = title;
        this.description = description;
        this.eventID = eventID;
    }

    public Agenda(Events event, Timestamp endTime, Timestamp startTime, String reason, String status, String description, String title, int eventID, int agendaID) {
        this.event = event;
        this.endTime = endTime;
        this.startTime = startTime;
        this.reason = reason;
        this.status = status;
        this.description = description;
        this.title = title;
        this.eventID = eventID;
        this.agendaID = agendaID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Events getEvent() {
        return event;
    }

    public void setEvent(Events event) {
        this.event = event;
    }

    public int getAgendaID() {
        return agendaID;
    }

    public void setAgendaID(int agendaID) {
        this.agendaID = agendaID;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
