package models;

import java.sql.Timestamp;

/**
 * Model class for meeting participants
 * Can be used for both club meetings and department meetings
 */
public class MeetingParticipant {
    private int participantID;
    private Integer clubMeetingID;     // NULL if participant is in a department meeting
    private Integer departmentMeetingID; // NULL if participant is in a club meeting
    private int userID;
    private String status; // e.g. Confirmed, Declined, Pending
    private Timestamp joinedAt;
    
    // Additional view fields
    private String userName;
    private String userEmail;
    private String userAvatar;
    private String roleName;
    private String meetingTitle;
    private String organizationName; // Club name or department name

    public MeetingParticipant() {
    }    // Constructor for club meeting participant
    public MeetingParticipant(int participantID, int clubMeetingID, int userID, String status, Timestamp joinedAt) {
        this.participantID = participantID;
        this.clubMeetingID = clubMeetingID;
        this.departmentMeetingID = null;
        this.userID = userID;
        this.status = status;
        this.joinedAt = joinedAt;
    }
    
    // Constructor for department meeting participant
    public MeetingParticipant(int participantID, Integer departmentMeetingID, int userID, String status, Timestamp joinedAt, boolean isDepartmentMeeting) {
        this.participantID = participantID;
        this.clubMeetingID = null;
        this.departmentMeetingID = departmentMeetingID;
        this.userID = userID;
        this.status = status;
        this.joinedAt = joinedAt;
    }

    public int getParticipantID() {
        return participantID;
    }

    public void setParticipantID(int participantID) {
        this.participantID = participantID;
    }    public Integer getClubMeetingID() {
        return clubMeetingID;
    }

    public void setClubMeetingID(Integer clubMeetingID) {
        this.clubMeetingID = clubMeetingID;
    }
    
    public Integer getDepartmentMeetingID() {
        return departmentMeetingID;
    }

    public void setDepartmentMeetingID(Integer departmentMeetingID) {
        this.departmentMeetingID = departmentMeetingID;
    }
    
    // Helper method to know if this participant is in a club meeting
    public boolean isClubMeeting() {
        return clubMeetingID != null;
    }
    
    // Helper method to know if this participant is in a department meeting
    public boolean isDepartmentMeeting() {
        return departmentMeetingID != null;
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
    }    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    
    public String getMeetingTitle() {
        return meetingTitle;
    }

    public void setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
    }
    
    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }    @Override
    public String toString() {
        return "MeetingParticipant{" +
                "participantID=" + participantID +
                ", clubMeetingID=" + clubMeetingID +
                ", departmentMeetingID=" + departmentMeetingID +
                ", userID=" + userID +
                ", status='" + status + '\'' +
                ", joinedAt=" + joinedAt +
                '}';
    }
}
