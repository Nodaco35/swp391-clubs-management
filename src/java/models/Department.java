
package models;

public class Department {
    private int departmentID;
    private String departmentName;
    private String description;
    private int clubID;

    public Department() {
    }

    public Department(int departmentID, String departmentName, String description, int clubID) {
        this.departmentID = departmentID;
        this.departmentName = departmentName;
        this.description = description;
        this.clubID = clubID;
    }

    public int getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getClubID() {
        return clubID;
    }

    public void setClubID(int clubID) {
        this.clubID = clubID;
    }

    @Override
    public String toString() {
        return "Department{" + "departmentID=" + departmentID + ", departmentName=" + departmentName + ", description=" + description + ", clubID=" + clubID + '}';
    }

}
