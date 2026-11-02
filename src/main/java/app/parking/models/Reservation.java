package app.parking.models;

import app.parking.enums.ReservationStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class Reservation {
    private final String reservationId;
    private final Person person;
    private final Vehicle vehicle;
    private final ParkingSpace parkingSpace;
    private final LocalDateTime startTime;
    private final LocalDateTime expectedEndTime;
    private LocalDateTime actualEndTime;
    private ReservationStatus status;

    public Reservation(Person person, Vehicle vehicle, ParkingSpace parkingSpace, int durationHours) {
        this.reservationId = UUID.randomUUID().toString();

        this.person = person;
        this.vehicle = vehicle;
        this.parkingSpace = parkingSpace;

        this.startTime = LocalDateTime.now();
        this.expectedEndTime = startTime.plusHours(durationHours);

        this.status = ReservationStatus.ACTIVE;
    }

    public String getReservationId() {
        return reservationId;
    }

    public Person getPerson() {
        return person;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpace getParkingSpace() {
        return parkingSpace;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getExpectedEndTime() {
        return expectedEndTime;
    }

    public LocalDateTime getActualEndTime() {
        return actualEndTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void completeReservation() {
        this.actualEndTime = LocalDateTime.now();
        this.status = ReservationStatus.COMPLETED;
    }
}
