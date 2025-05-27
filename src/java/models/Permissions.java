package models;

public class Permissions {
    private int permissionID;
    private String permissionName;
    private String description;

    public Permissions() {}

    public Permissions(int permissionID, String permissionName, String description) {
        this.permissionID = permissionID;
        this.permissionName = permissionName;
        this.description = description;
    }

    public int getPermissionID() { return permissionID; }
    public void setPermissionID(int permissionID) { this.permissionID = permissionID; }

    public String getPermissionName() { return permissionName; }
    public void setPermissionName(String permissionName) { this.permissionName = permissionName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
