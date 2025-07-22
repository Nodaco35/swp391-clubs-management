
package models;

import java.util.List;

public class ClubDepartmentDetail {
    private int clubDepartmentID;
    private String departmentName;
    private List<DepartmentMember> members;

    public ClubDepartmentDetail() {
    }

    public int getClubDepartmentID() {
        return clubDepartmentID;
    }

    public void setClubDepartmentID(int clubDepartmentID) {
        this.clubDepartmentID = clubDepartmentID;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public List<DepartmentMember> getMembers() {
        return members;
    }

    public void setMembers(List<DepartmentMember> members) {
        this.members = members;
    }
    
    
}
