package app.parking.models;

import java.util.List;

public class ParkingArea {
    private final String areaName;
    private final List<ParkingSpace> parkingSpaces;

    public ParkingArea(String areaName, List<ParkingSpace> parkingSpaces) {
        this.areaName = areaName;
        this.parkingSpaces = parkingSpaces;
    }

    public String getAreaName() {
        return areaName;
    }

    public List<ParkingSpace> getParkingSpaces() {
        return parkingSpaces;
    }
}