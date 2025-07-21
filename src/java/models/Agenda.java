package models;

import java.sql.Timestamp;

public class Agenda {
    private int agendaID;
    private int scheduleID; // Thay eventID bằng scheduleID
    private String title;
    private String description;
    private String status;
    private String reason;
    private Timestamp startTime;
    private Timestamp endTime;
    private EventSchedule eventSchedule; // Thay Events bằng EventSchedule

    // Constructor mặc định
    public Agenda() {
    }

    // Constructor đầy đủ
    public Agenda(int agendaID, int scheduleID, String title, String description,
                  String status, String reason, Timestamp startTime, Timestamp endTime,
                  EventSchedule eventSchedule) {
        this.agendaID = agendaID;
        this.scheduleID = scheduleID;
        this.title = title;
        this.description = description;
        this.status = status;
        this.reason = reason;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventSchedule = eventSchedule;
    }

    // Getters và Setters
    public int getAgendaID() {
        return agendaID;
    }

    public void setAgendaID(int agendaID) {
        this.agendaID = agendaID;
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(int scheduleID) {
        this.scheduleID = scheduleID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public EventSchedule getEventSchedule() {
        return eventSchedule;
    }

    public void setEventSchedule(EventSchedule eventSchedule) {
        this.eventSchedule = eventSchedule;
    }
}