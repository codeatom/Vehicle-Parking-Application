package app.parking.models;

import java.util.List;

public class ParkingArea {
    private final String areaId;
    private final String areaName;
    private final List<ParkingSpace> parkingSpaces;

    public ParkingArea(String areaId, String areaName, List<ParkingSpace> parkingSpaces) {
        this.areaId = areaId;
        this.areaName = areaName;
        this.parkingSpaces = parkingSpaces;
    }

    public String getAreaId() {
        return areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public List<ParkingSpace> getParkingSpaces() {
        return parkingSpaces;
    }
}