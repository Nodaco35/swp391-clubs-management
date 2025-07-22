package models;
import java.sql.Date;
import java.sql.Time;

public class EventSchedule {
    private int scheduleID;
    private int eventID;
    private Date eventDate;
    private int locationID;
    private Time startTime;
    private Time endTime;
    private Locations location; // Tham chiếu đến địa điểm

    // Constructor mặc định
    public EventSchedule() {
    }

    // Constructor đầy đủ
    public EventSchedule(int scheduleID, int eventID, Date eventDate, int locationID,
                         Time startTime, Time endTime, Locations location) {
        this.scheduleID = scheduleID;
        this.eventID = eventID;
        this.eventDate = eventDate;
        this.locationID = locationID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }

    // Getters và Setters
    public int getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(int scheduleID) {
        this.scheduleID = scheduleID;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Locations getLocation() {
        return location;
    }

    public void setLocation(Locations location) {
        this.location = location;
    }
}
