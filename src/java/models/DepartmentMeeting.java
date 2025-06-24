
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import java.sql.Timestamp;
/**
 *
 * @author he181
 */
public class DepartmentMeeting {
    private int departmentMeetingID;
    private int clubDepartmentID;
    private String URLMeeting;
    private Timestamp startedTime;
    private String departmentName;
    private String clubName;
    private String clubImg;
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

    public String getURLMeeting() {
        return URLMeeting;
    }

    public void setURLMeeting(String URLMeeting) {
        this.URLMeeting = URLMeeting;
    }

    public Timestamp getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(Timestamp statedTime) {
        this.startedTime = statedTime;
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

    
    
    
            
            
            
}
