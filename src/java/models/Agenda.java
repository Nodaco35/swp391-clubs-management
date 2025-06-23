package models;

import java.sql.Timestamp;

public class Agenda {

    private int agendaID;
    private int eventID;
    private String title;
    private String description;
    private Timestamp startTime;
    private Timestamp endTime;

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
