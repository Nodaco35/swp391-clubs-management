package models;

import java.util.Date;

public class ClubCreationPermissions {

    private int permissionID;
    private String userID;
    private String status;
    private String grantedBy;
    private Date grantedDate;
    private Date usedDate;

    public ClubCreationPermissions() {
    }

    public ClubCreationPermissions(int permissionID, String userID, String status, String grantedBy, Date grantedDate, Date usedDate) {
        this.permissionID = permissionID;
        this.userID = userID;
        this.status = status;
        this.grantedBy = grantedBy;
        this.grantedDate = grantedDate;
        this.usedDate = usedDate;
    }

    public int getPermissionID() {
        return permissionID;
    }

    public void setPermissionID(int permissionID) {
        this.permissionID = permissionID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGrantedBy() {
        return grantedBy;
    }

    public void setGrantedBy(String grantedBy) {
        this.grantedBy = grantedBy;
    }

    public Date getGrantedDate() {
        return grantedDate;
    }

    public void setGrantedDate(Date grantedDate) {
        this.grantedDate = grantedDate;
    }

    public Date getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(Date usedDate) {
        this.usedDate = usedDate;
    }

}
