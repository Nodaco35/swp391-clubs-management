
package models;

import java.security.Timestamp;
import java.util.Date;

public class PeriodicReportEvents {

    private int reportEventID;
    private int reportID;
    private String eventName;
    private Date eventDate;
    private String eventType;
    private int participantCount;
    private String proofLink;

    public PeriodicReportEvents() {
    }

    public int getReportEventID() {
        return reportEventID;
    }

    public void setReportEventID(int reportEventID) {
        this.reportEventID = reportEventID;
    }

    public int getReportID() {
        return reportID;
    }

    public void setReportID(int reportID) {
        this.reportID = reportID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    public String getProofLink() {
        return proofLink;
    }

    public void setProofLink(String proofLink) {
        this.proofLink = proofLink;
    }
    
    
}
