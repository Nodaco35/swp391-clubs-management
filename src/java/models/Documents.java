package models;

public class Documents {
    private int documentID;
    private String documentName;
    private String description;
    private String documentURL;
    private String documentType;
    private Clubs club;
    private Department department;

    public Documents() {
    }

    public Documents(int documentID, String documentName, String description, String documentURL, String documentType, Clubs club, Department department) {
        this.documentID = documentID;
        this.documentName = documentName;
        this.description = description;
        this.documentURL = documentURL;
        this.documentType = documentType;
        this.club = club;
        this.department = department;
    }

    public int getDocumentID() {
        return documentID;
    }

    public void setDocumentID(int documentID) {
        this.documentID = documentID;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocumentURL() {
        return documentURL;
    }

    public void setDocumentURL(String documentURL) {
        this.documentURL = documentURL;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public Clubs getClub() {
        return club;
    }

    public void setClub(Clubs club) {
        this.club = club;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
