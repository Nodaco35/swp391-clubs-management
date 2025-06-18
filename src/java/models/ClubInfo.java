package models;

public class ClubInfo {
    private int clubID;
    private String clubName;
    private String clubImg;
    private String clubChairmanName;

    public ClubInfo() {
    }

    public ClubInfo(int clubID, String clubImg, String clubChairmanName, String clubName) {
        this.clubID = clubID;
        this.clubImg = clubImg;
        this.clubChairmanName = clubChairmanName;
        this.clubName = clubName;
    }

    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }

    public String getClubChairmanName() {
        return clubChairmanName;
    }

    public void setClubChairmanName(String clubChairmanName) {
        this.clubChairmanName = clubChairmanName;
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
