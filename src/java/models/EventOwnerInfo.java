package models;

public class EventOwnerInfo {
    private String fullName;
    private String email;
    private String clubName;

    public EventOwnerInfo(String fullName, String email, String clubName) {
        this.fullName = fullName;
        this.email = email;
        this.clubName = clubName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getClubName() {
        return clubName;
    }
}

