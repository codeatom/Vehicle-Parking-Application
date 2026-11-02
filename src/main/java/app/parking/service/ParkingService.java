package app.parking.service;

import app.parking.enums.*;
import app.parking.models.*;
import app.parking.repos.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingService {
    private final PersonRepository personRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final ReservationRepository reservationRepository;
    private final ParkingAreaRepository parkingAreaRepository;

    public ParkingService(
            PersonRepository personRepository,
            VehicleRepository vehicleRepository,
            ParkingSpaceRepository parkingSpaceRepository,
            ReservationRepository reservationRepository,
            ParkingAreaRepository parkingAreaRepository) {

        this.personRepository = personRepository;
        this.vehicleRepository = vehicleRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.reservationRepository = reservationRepository;
        this.parkingAreaRepository = parkingAreaRepository;
    }

    public ParkingArea initParkingAreaAndSpaces(String areaId, String areaName, int totalSpaces) {
        List <ParkingSpace> parkingSpaces = new ArrayList<>();

        for (int i = 1; i <= totalSpaces; i++) {
            String spaceId = "P" + i;
            ParkingSpace parkingSpace = new ParkingSpace(spaceId);
            parkingSpaces.add(parkingSpace);
            parkingSpaceRepository.saveParkingSpace(parkingSpace);
        }

        ParkingArea parkingArea = new ParkingArea(areaId, areaName, parkingSpaces);
        parkingAreaRepository.saveParkingArea(parkingArea);

        return parkingArea;
    }

    public Person registerPerson(String name, String phoneNumber, PersonType type) {
        Person person = new Person(name, phoneNumber, type);
        personRepository.savePerson(person);

        return person;
    }

    public Vehicle registerVehicle(String registrationNumber, Person person) {
        Vehicle vehicle = new Vehicle(registrationNumber, person);
        vehicleRepository.saveVehicle(vehicle);

        return vehicle;
    }

    // Display available parking spaces
    public void displayParkingSpaces(String areaId) {
        System.out.println("PARKING SPACE STATUS");

        ParkingArea parkingArea =  parkingAreaRepository.findById(areaId);

        for (ParkingSpace space : parkingArea.getParkingSpaces()) {
            System.out.println(space.getSpaceId() + " : " + space.getStatus());
        }
    }

    // Get available parking spaces
    public List<ParkingSpace> getAvailableSpaces(String id) {
        return parkingAreaRepository.findAvailableSpaces(id);
    }

    // Create reservation
    public Reservation createReservation(
            String name,
            PersonType type,
            String phoneNumber,
            String registrationNumber,
            String areaId,
            String spaceId,
            int durationHours) {

        if (durationHours <= 0) {
            throw new IllegalArgumentException("Duration must be greater than zero.");
        }

        Person person = registerPerson(name, phoneNumber, type);
        Vehicle vehicle = registerVehicle(registrationNumber, person);

        ParkingSpace parkingSpace = parkingAreaRepository.findById(areaId)
                .getParkingSpaces()
                .stream()
                .filter(space -> space.getSpaceId().equalsIgnoreCase(spaceId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Parking space not found"));

        if (parkingSpace.getStatus() != ParkingSpaceStatus.AVAILABLE) {
            throw new IllegalArgumentException("Parking space is occupied.");
        }

        Reservation reservation = new Reservation(person, vehicle, parkingSpace, durationHours);

        parkingSpace.occupy();

        reservationRepository.saveReservation(reservation);

        return reservation;
    }

    // Complete reservation
    public void completeReservation(String reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId);

        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found.");
        }

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new IllegalArgumentException("Reservation already completed.");
        }

        reservation.completeReservation();
        reservation.getParkingSpace().release();

        System.out.println("Reservation completed successfully.");
        System.out.println("Parking space " + reservation.getParkingSpace().getSpaceId() + " is now available.");
    }

    // Display active reservations
    public void displayActiveReservations() {
        System.out.println("\n--- ACTIVE RESERVATIONS ---");

        for (Reservation reservation : reservationRepository.findActiveReservations()) {
            System.out.println("Reservation ID: " + reservation.getReservationId());
            System.out.println("Person: " + reservation.getPerson().getName());
            System.out.println("Vehicle: " + reservation.getVehicle().getRegistrationNumber());
            System.out.println("Parking Space: " + reservation.getParkingSpace().getSpaceId());
            System.out.println("Start Time: " + reservation.getStartTime());
            System.out.println("Expected End Time: " + reservation.getExpectedEndTime());
            System.out.println("-------------------------");
        }
    }
}
