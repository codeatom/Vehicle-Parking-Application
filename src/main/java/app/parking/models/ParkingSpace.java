package app.parking.models;

import app.parking.enums.ParkingSpaceStatus;

public class ParkingSpace {
    private final String spaceId;
    private ParkingSpaceStatus status;

    public ParkingSpace(String spaceId) {
        this.spaceId = spaceId;
        this.status = ParkingSpaceStatus.AVAILABLE;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public ParkingSpaceStatus getStatus() {
        return status;
    }

    /*public void setStatus(ParkingSpaceStatus status) {
        this.status = status;
    }*/

    public void occupy() {
        if (status == ParkingSpaceStatus.OCCUPIED) {
            throw new IllegalStateException(
                    "Parking space is already occupied.");
        }
        status = ParkingSpaceStatus.OCCUPIED;
    }

    public void release() {
        if (status == ParkingSpaceStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "Parking space is already available.");
        }
        status = ParkingSpaceStatus.AVAILABLE;
    }

}