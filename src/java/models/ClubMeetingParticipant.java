package models;

import java.sql.Timestamp;

public class ClubMeetingParticipant {
    private int participantID;
    private int meetingID; // References Meeting.meetingID
    private int userID;
    private String status; // e.g. Confirmed, Declined, Pending
    private Timestamp joinedAt;
    
    // Additional view fields
    private String userName;
    private String userEmail;
    private String userAvatar;
    private String roleName;
    private String meetingTitle;

    public ClubMeetingParticipant() {
    }

    public ClubMeetingParticipant(int participantID, int meetingID, int userID, String status, Timestamp joinedAt) {
        this.participantID = participantID;
        this.meetingID = meetingID;
        this.userID = userID;
        this.status = status;
        this.joinedAt = joinedAt;
    }

    public int getParticipantID() {
        return participantID;
    }

    public void setParticipantID(int participantID) {
        this.participantID = participantID;
    }

    public int getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(int meetingID) {
        this.meetingID = meetingID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Timestamp joinedAt) {
        this.joinedAt = joinedAt;
    }
    
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    public String getMeetingTitle() {
        return meetingTitle;
    }

    public void setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
    }

    @Override
    public String toString() {
        return "ClubMeetingParticipant{" +
                "participantID=" + participantID +
                ", meetingID=" + meetingID +
                ", userID=" + userID +
                ", status='" + status + '\'' +
                ", joinedAt=" + joinedAt +
                '}';
    }
}
