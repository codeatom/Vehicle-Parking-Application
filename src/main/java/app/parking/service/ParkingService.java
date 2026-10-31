package app.parking.service;

import app.parking.enums.*;
import app.parking.models.*;
import app.parking.repos.*;
import java.util.List;

public class ParkingService {

    private final PersonRepository personRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final ReservationRepository reservationRepository;

    public ParkingService(
            PersonRepository personRepository,
            VehicleRepository vehicleRepository,
            ParkingSpaceRepository parkingSpaceRepository,
            ReservationRepository reservationRepository) {

        this.personRepository = personRepository;
        this.vehicleRepository = vehicleRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.reservationRepository = reservationRepository;
    }

    public Person registerPerson(String name, String phoneNumber, PersonType type, String registrationNumber) {

        // Check if person already exists
        if (personRepository.findById(phoneNumber) != null) {
            throw new IllegalArgumentException("Phone number already registered.");
        }

        // Check if vehicle already exists
        if (vehicleRepository.findById(registrationNumber) != null) {
            throw new IllegalArgumentException("Vehicle already registered.");
        }

        // Create Person
        Person person = new Person(name, phoneNumber, type);

        // Create Vehicle associated with Person
        Vehicle vehicle = new Vehicle(registrationNumber, person);

        // Save both objects
        personRepository.save(phoneNumber, person);
        vehicleRepository.save(registrationNumber, vehicle);

        return person;
    }

    // Register a vehicle
    public Vehicle registerVehicle(String registrationNumber, Person person) {
        if (vehicleRepository.findById(registrationNumber) != null) {
            throw new IllegalArgumentException("Vehicle already registered.");
        }

        Vehicle vehicle = new Vehicle(registrationNumber, person);
        vehicleRepository.saveVehicle(vehicle);

        return vehicle;
    }

    // Display available parking spaces
    public void displayParkingSpaces() {
        System.out.println("\n--- PARKING SPACE STATUS ---");

        for (ParkingSpace space : parkingSpaceRepository.findAll().values()) {
            System.out.println(space.getSpaceId() + " : " + space.getStatus());
        }
    }

    // Get available parking spaces
    public List<ParkingSpace> getAvailableSpaces() {
        return parkingSpaceRepository.findAvailableSpaces();
    }

    // Create reservation
    public Reservation createReservation(String phoneNumber, String registrationNumber, String spaceId, int durationHours) {

        if (durationHours <= 0) {
            throw new IllegalArgumentException("Duration must be greater than zero.");
        }

        Person person = personRepository.findById(phoneNumber);

        if (person == null) {
            throw new IllegalArgumentException(
                    "Person not registered.");
        }

        Vehicle vehicle = vehicleRepository.findById(registrationNumber);

        if (vehicle == null || vehicle.getOwner() != person) {
            throw new IllegalArgumentException("Vehicle not registered to this person.");
        }

        ParkingSpace space = parkingSpaceRepository.findById(spaceId.toUpperCase().trim());

        if (space == null) {
            throw new IllegalArgumentException("Parking space does not exist.");
        }

        if (space.getStatus() != ParkingSpaceStatus.AVAILABLE) {
            throw new IllegalArgumentException("Parking space is occupied.");
        }

        Reservation reservation = new Reservation(person, vehicle, space, durationHours);

        space.occupy();

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
