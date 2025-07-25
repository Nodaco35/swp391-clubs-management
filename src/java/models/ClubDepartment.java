package models;

/**
 * Model class for ClubDepartments table with Departments relationship
 */
public class ClubDepartment {
    private int clubDepartmentId;
    private int departmentId;
    private String departmentName;
    private boolean departmentStatus;
    private String description;
    private int clubId;
    
    public ClubDepartment() {
        // Default constructor
    }

    public ClubDepartment(int clubId, String description, boolean departmentStatus, String departmentName, int departmentId, int clubDepartmentId) {
        this.clubId = clubId;
        this.description = description;
        this.departmentStatus = departmentStatus;
        this.departmentName = departmentName;
        this.departmentId = departmentId;
        this.clubDepartmentId = clubDepartmentId;
    }

    public int getClubDepartmentId() {
        return clubDepartmentId;
    }

    public void setClubDepartmentId(int clubDepartmentId) {
        this.clubDepartmentId = clubDepartmentId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public boolean isDepartmentStatus() {
        return departmentStatus;
    }

    public void setDepartmentStatus(boolean departmentStatus) {
        this.departmentStatus = departmentStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getClubId() {
        return clubId;
    }

    public void setClubId(int clubId) {
        this.clubId = clubId;
    }
      @Override
    public String toString() {
        return "ClubDepartment{" + 
                "clubDepartmentId=" + clubDepartmentId +
                ", departmentId=" + departmentId + 
                ", departmentName='" + departmentName + '\'' + 
                ", departmentStatus=" + departmentStatus + 
                ", description='" + description + '\'' + 
                ", clubId=" + clubId + 
                '}';
    }
}
