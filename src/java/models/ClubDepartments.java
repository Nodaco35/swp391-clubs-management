
package models;

public class ClubDepartments {
    private int departmentID;
    private String departmentName;
    private boolean departmentStatus;
    private String description;
    private int clubID;

    public ClubDepartments() {}

    public ClubDepartments(int departmentID, String departmentName, boolean departmentStatus, String description, int clubID) {
        this.departmentID = departmentID;
        this.departmentName = departmentName;
        this.departmentStatus = departmentStatus;
        this.description = description;
        this.clubID = clubID;
    }

    public int getDepartmentID() { return departmentID; }
    public void setDepartmentID(int departmentID) { this.departmentID = departmentID; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public boolean isDepartmentStatus() { return departmentStatus; }
    public void setDepartmentStatus(boolean departmentStatus) { this.departmentStatus = departmentStatus; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getClubID() { return clubID; }
    public void setClubID(int clubID) { this.clubID = clubID; }
}

