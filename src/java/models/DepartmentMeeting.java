package models;

import java.sql.Timestamp;
import java.util.List;

/**
 * Model class for department meetings
 * Represents meetings held within a department
 * Updated to match database structure
 */
public class DepartmentMeeting {
    // Các trường thực tế từ database
    private int departmentMeetingID;
    private int departmentID;
    private String urlMeeting;
    private Timestamp startedTime;
    
    // Additional view fields (không lưu trong database)
    private String departmentName;
    private String createdByName;
    private int participantCount;
    
    // Danh sách người tham gia cuộc họp
    private List<DepartmentMeetingParticipant> participants;

    public DepartmentMeeting() {
    }
    
    public DepartmentMeeting(int departmentMeetingID, int departmentID, String urlMeeting, Timestamp startedTime) {
        this.departmentMeetingID = departmentMeetingID;
        this.departmentID = departmentID;
        this.urlMeeting = urlMeeting;
        this.startedTime = startedTime;
    }

    public int getDepartmentMeetingID() {
        return departmentMeetingID;
    }

    public void setDepartmentMeetingID(int departmentMeetingID) {
        this.departmentMeetingID = departmentMeetingID;
    }
    
    // Methods for compatibility with existing code
    public int getMeetingID() {
        return departmentMeetingID;
    }

    public void setMeetingID(int meetingID) {
        this.departmentMeetingID = meetingID;
    }

    public int getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }

    public String getTitle() {
        return "Meeting " + departmentMeetingID; // Default title based on ID
    }

    public void setTitle(String title) {
        // Không lưu trữ, chỉ để tương thích
    }

    public String getDescription() {
        return null; // Không có trường tương ứng
    }

    public void setDescription(String description) {
        // Không lưu trữ, chỉ để tương thích
    }

    public Timestamp getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(Timestamp startedTime) {
        this.startedTime = startedTime;
    }
    
    // Methods for compatibility with existing code
    public Timestamp getScheduledAt() {
        return startedTime;
    }

    public void setScheduledAt(Timestamp scheduledAt) {
        this.startedTime = scheduledAt;
    }
    
    public String getUrlMeeting() {
        return urlMeeting;
    }

    public void setUrlMeeting(String urlMeeting) {
        this.urlMeeting = urlMeeting;
    }

    public int getDuration() {
        return 60; // Default 60 phút
    }

    public void setDuration(int duration) {
        // Không lưu trữ, chỉ để tương thích
    }

    public String getLocation() {
        return urlMeeting; // Sử dụng urlMeeting như location
    }

    public void setLocation(String location) {
        this.urlMeeting = location;
    }

    public int getCreatedByID() {
        return 0; // Default value
    }

    public void setCreatedByID(int createdByID) {
        // Không lưu trữ, chỉ để tương thích
    }

    public Timestamp getCreatedAt() {
        return startedTime; // Sử dụng startedTime thay thế
    }

    public void setCreatedAt(Timestamp createdAt) {
        // Không lưu trữ, chỉ để tương thích
    }

    public String getStatus() {
        return "Scheduled"; // Default status
    }

    public void setStatus(String status) {
        // Không lưu trữ, chỉ để tương thích
    }
    
    public List<DepartmentMeetingParticipant> getParticipants() {
        return participants;
    }
    
    public void setParticipants(List<DepartmentMeetingParticipant> participants) {
        this.participants = participants;
        if (participants != null) {
            this.participantCount = participants.size();
        }
    }
    
    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
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
    }

    @Override
    public String toString() {
        return "DepartmentMeeting{" +
                "departmentMeetingID=" + departmentMeetingID +
                ", departmentID=" + departmentID +
                ", urlMeeting='" + urlMeeting + '\'' +
                ", startedTime=" + startedTime +
                ", departmentName='" + departmentName + '\'' +
                ", createdByName='" + createdByName + '\'' +
                ", participantCount=" + participantCount +
                '}';
    }
}
