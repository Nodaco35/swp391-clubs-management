package models;

public class Permission {
    private int permissionID;
    private String permissionName;

    public Permission() {
    }

    public Permission(int permissionID, String permissionName) {
        this.permissionID = permissionID;
        this.permissionName = permissionName;
    }

    public int getPermissionID() {
        return permissionID;
    }

    public void setPermissionID(int permissionID) {
        this.permissionID = permissionID;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    @Override
    public String toString() {
        return "Permission{" + "permissionID=" + permissionID + ", permissionName=" + permissionName + '}';
    }
    
    
}