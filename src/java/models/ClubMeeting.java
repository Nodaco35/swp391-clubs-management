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
public class ClubMeeting {
    private int clubMeetingID;
    private int clubID;
    private String URLMeeting;
    private Timestamp startedTime;
    private String clubName;
    private String clubImg;

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
    
    @Override
    public String toString() {
        return "ClubMeeting{" + "clubMeetingID=" + clubMeetingID + ", clubID=" + clubID + ", URLMeeting=" + URLMeeting + ", startedTime=" + startedTime + ", clubName=" + clubName + '}';
    }
}
