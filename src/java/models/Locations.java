package models;

public class Locations {
    private int locationID;
    private String locationName;
    private String typeLocation;

    public Locations() {
    }

    public Locations(int locationID, String locationName, String typeLocation) {
        this.locationID = locationID;
        this.locationName = locationName;
        this.typeLocation = typeLocation;
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getTypeLocation() {
        return typeLocation;
    }

    public void setTypeLocation(String typeLocation) {
        this.typeLocation = typeLocation;
    }
}
