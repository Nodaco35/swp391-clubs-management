package models;
import java.sql.Timestamp;
import java.util.List;

public class DepartmentMeeting {
    private int departmentMeetingID;
    private int clubDepartmentID;
    private String title;
    private String urlMeeting;
    private String documentLink;
    private Timestamp startedTime;
    private String departmentName;
    private String clubName;
    private String clubImg;
    private List<String> participantUserIds;

    public DepartmentMeeting() {
    }

    public int getDepartmentMeetingID() {
        return departmentMeetingID;
    }

    public void setDepartmentMeetingID(int departmentMeetingID) {
        this.departmentMeetingID = departmentMeetingID;
    }

    public int getClubDepartmentID() {
        return clubDepartmentID;
    }

    public void setClubDepartmentID(int clubDepartmentID) {
        this.clubDepartmentID = clubDepartmentID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getURLMeeting() {
        return urlMeeting;
    }

    public void setURLMeeting(String urlMeeting) {
        this.urlMeeting = urlMeeting;
    }

    public String getDocumentLink() {
        return documentLink;
    }

    public void setDocumentLink(String documentLink) {
        this.documentLink = documentLink;
    }

    public Timestamp getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(Timestamp startedTime) {
        this.startedTime = startedTime;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
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

    public List<String> getParticipantUserIds() {
        return participantUserIds;
    }

    public void setParticipantUserIds(List<String> participantUserIds) {
        this.participantUserIds = participantUserIds;
    }
}