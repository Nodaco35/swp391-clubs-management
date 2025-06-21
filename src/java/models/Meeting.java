package models;

import java.sql.Timestamp;

/**
 * Model class for club meetings
 * Represents meetings held within a club
 */
public class Meeting {
    private int meetingID;
    private int clubID;
    private String title;
    private String description;
    private Timestamp scheduledAt;
    private int duration; // in minutes
    private String location;
    private int createdByID; // User ID who created the meeting
    private Timestamp createdAt;
    private String status; // e.g., "Scheduled", "Cancelled", "Completed"
    
    // Additional view fields (not stored in database)
    private String clubName;
    private String createdByName;
    private int participantCount;

    public Meeting() {
    }    public Meeting(int meetingID, int clubID, String title, String description, Timestamp scheduledAt, 
                  int duration, String location, int createdByID, Timestamp createdAt, String status) {
        this.meetingID = meetingID;
        this.clubID = clubID;
        this.title = title;
        this.description = description;
        this.scheduledAt = scheduledAt;
        this.duration = duration;
        this.location = location;
        this.createdByID = createdByID;
        this.createdAt = createdAt;
        this.status = status;
    }

    public int getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(int meetingID) {
        this.meetingID = meetingID;
    }    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
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

    public Timestamp getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Timestamp scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCreatedByID() {
        return createdByID;
    }

    public void setCreatedByID(int createdByID) {
        this.createdByID = createdByID;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
      public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }    @Override
    public String toString() {
        return "Meeting{" +
                "meetingID=" + meetingID +
                ", clubID=" + clubID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", scheduledAt=" + scheduledAt +
                ", duration=" + duration +
                ", location='" + location + '\'' +
                ", createdByID=" + createdByID +
                ", createdAt=" + createdAt +
                ", status='" + status + '\'' +
                '}';
    }
}
