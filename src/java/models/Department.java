package models;

public class Department {
    private int departmentID;
    private String departmentName;
    private boolean departmentStatus;
    private String description;

    public Department() {
    }

    public Department(int departmentID, String departmentName, boolean departmentStatus, String description) {
        this.departmentID = departmentID;
        this.departmentName = departmentName;
        this.departmentStatus = departmentStatus;
        this.description = description;
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

    @Override
    public String toString() {
        return "Department{" +
               "departmentID=" + departmentID +
               ", departmentName='" + departmentName + '\'' +
               ", departmentStatus=" + departmentStatus +
               ", description='" + description + '\'' +
               '}';
    }
}