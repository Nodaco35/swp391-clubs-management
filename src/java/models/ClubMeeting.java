/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import java.sql.Timestamp;
import java.util.List;
/**
 *
 * @author he181
 */
public class ClubMeeting {
    private int clubMeetingID;
    private int clubID;
    private String URLMeeting;
    private Timestamp startedTime;
    private String clubName;
    private String clubImg;
    
    private Timestamp endTime;
    private String meetingTitle;
    private String description;
    private String document;
    
    List<String> participantClubDepartmentIds;
    

    public ClubMeeting() {
    }

    public int getClubMeetingID() {
        return clubMeetingID;
    }

    public void setClubMeetingID(int clubMeetingID) {
        this.clubMeetingID = clubMeetingID;
    }

    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }

    public String getURLMeeting() {
        return URLMeeting;
    }

    public void setURLMeeting(String URLMeeting) {
        this.URLMeeting = URLMeeting;
    }

    public Timestamp getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(Timestamp startedTime) {
        this.startedTime = startedTime;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getClubImg() {
        return clubImg;
    }

    public void setClubImg(String clubImg) {
        this.clubImg = clubImg;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getMeetingTitle() {
        return meetingTitle;
    }

    public void setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public List<String> getParticipantClubDepartmentIds() {
        return participantClubDepartmentIds;
    }

    public void setParticipantClubDepartmentIds(List<String> participantClubDepartmentIds) {
        this.participantClubDepartmentIds = participantClubDepartmentIds;
    }
    
    

    @Override
    public String toString() {
        return "ClubMeeting{" + "clubMeetingID=" + clubMeetingID + ", clubID=" + clubID + ", URLMeeting=" + URLMeeting + ", startedTime=" + startedTime + ", clubName=" + clubName + ", clubImg=" + clubImg + ", endTime=" + endTime + ", meetingTitle=" + meetingTitle + ", description=" + description + ", document=" + document + '}';
    }
    
    
    
    
    
}
