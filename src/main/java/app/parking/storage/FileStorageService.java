
package app.parking.storage;

import app.parking.enums.*;
import app.parking.models.*;
import app.parking.repos.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileStorageService {
    private static final String DATA_DIR = "data";

    private static final String PERSON_FILE = DATA_DIR + "/persons.txt";
    private static final String VEHICLE_FILE = DATA_DIR + "/vehicles.txt";
    private static final String SPACE_FILE = DATA_DIR + "/parking_spaces.txt";
    private static final String AREA_FILE = DATA_DIR + "/parking_areas.txt";
    private static final String RESERVATION_FILE = DATA_DIR + "/reservations.txt";

    private final PersonRepository personRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final ReservationRepository reservationRepository;
    private final ParkingAreaRepository parkingAreaRepository;

    public FileStorageService(
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

    // Load saved data
    public void initialize() throws IOException {
        Files.createDirectories(Paths.get(DATA_DIR));
        loadPersons();
        loadVehicles();
        loadParkingSpaces();
        loadParkingAreas();
        loadReservations();
    }

    // save data to file
    public void saveAll() throws IOException {
        Files.createDirectories(Paths.get(DATA_DIR));
        savePersons();
        saveVehicles();
        saveParkingSpaces();
        saveParkingAreas();
        saveReservations();
    }

    private void savePersons() throws IOException {
        Path file = Paths.get(PERSON_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {

            for (Person person : personRepository.findAll().values()) {
                writer.write(person.getName() + "|" + person.getPhoneNumber() + "|" + person.getPersonType());
                writer.newLine();
            }
        }
    }

    private void saveVehicles() throws IOException {
        Path file = Paths.get(VEHICLE_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {

            for (Vehicle vehicle : vehicleRepository.findAll().values()) {
                writer.write(vehicle.getRegistrationNumber() + "|" + vehicle.getOwner().getPhoneNumber());
                writer.newLine();
            }
        }
    }

    private void saveParkingSpaces() throws IOException {
        Path file = Paths.get(SPACE_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {

            for (ParkingSpace space : parkingSpaceRepository.findAll().values()) {
                writer.write(space.getSpaceId() + "|" + space.getStatus());
                writer.newLine();
            }
        }
    }

    private void saveParkingAreas() throws IOException {
        Path file = Paths.get(AREA_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {

            for (ParkingArea area : parkingAreaRepository.findAll().values()) {
                StringBuilder spaceIds = new StringBuilder();

                for (ParkingSpace space : area.getParkingSpaces()) {
                    if (!spaceIds.isEmpty()) {
                        spaceIds.append(",");
                    }

                    spaceIds.append(space.getSpaceId());
                }

                writer.write(area.getAreaId() + "|" + area.getAreaName() + "|" + spaceIds);
                writer.newLine();
            }
        }
    }

    private void saveReservations() throws IOException {
        Path file = Paths.get(RESERVATION_FILE);
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {

            for (Reservation reservation : reservationRepository.findAll().values()) {
                writer.write(
                        reservation.getReservationId() + "|" +
                                reservation.getPerson().getPhoneNumber() + "|" +
                                reservation.getVehicle().getRegistrationNumber() + "|" +
                                reservation.getParkingSpace().getSpaceId() + "|" +
                                reservation.getStartTime() + "|" +
                                reservation.getExpectedEndTime() + "|" +
                                (reservation.getActualEndTime() == null
                                        ? ""
                                        : reservation.getActualEndTime()) + "|" +
                                reservation.getStatus()
                );
                writer.newLine();
            }
        }
    }

    private void loadPersons() throws IOException {
        Path file = Paths.get(PERSON_FILE);
        if (!Files.exists(file)) return;

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);

                if (parts.length != 3) {
                    throw new IOException("Invalid person data: " + line);
                }

                Person person = new Person(parts[0], parts[1], PersonType.valueOf(parts[2]));
                personRepository.savePerson(person);
            }
        }
    }

    private void loadVehicles() throws IOException {
        Path file = Paths.get(VEHICLE_FILE);
        if (!Files.exists(file)) return;

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);

                if (parts.length != 2) {
                    throw new IOException("Invalid vehicle data: " + line);
                }

                Person owner = personRepository.findById(parts[1]);

                if (owner == null) {
                    throw new IOException("Vehicle owner not found: " + parts[1]);
                }

                Vehicle vehicle = new Vehicle(parts[0], owner);
                vehicleRepository.saveVehicle(vehicle);
            }
        }
    }

    private void loadParkingSpaces() throws IOException {
        Path file = Paths.get(SPACE_FILE);
        if (!Files.exists(file)) return;

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);

                if (parts.length != 2) {
                    throw new IOException("Invalid parking space data: " + line);
                }

                // Validate the stored status.
                ParkingSpaceStatus.valueOf(parts[1]);

                ParkingSpace space = new ParkingSpace(parts[0]);  // When loading parking spaces, they are created as AVAILABLE
                parkingSpaceRepository.saveParkingSpace(space);
            }
        }
    }

    private void loadParkingAreas() throws IOException {
        Path file = Paths.get(AREA_FILE);
        if (!Files.exists(file)) return;

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);

                if (parts.length != 3) {
                    throw new IOException("Invalid parking area data: " + line);
                }

                String areaId = parts[0];
                String areaName = parts[1];

                List<ParkingSpace> spaces = new ArrayList<>();

                if (!parts[2].isEmpty()) {
                    String[] spaceIds = parts[2].split(",");

                    for (String spaceId : spaceIds) {
                        ParkingSpace space = parkingSpaceRepository.findById(spaceId);

                        if (space == null) {
                            throw new IOException("Parking space not found: " + spaceId);
                        }

                        spaces.add(space);
                    }
                }

                ParkingArea area = new ParkingArea(areaId, areaName, spaces);
                parkingAreaRepository.saveParkingArea(area);
            }
        }
    }

    private void loadReservations() throws IOException {
        Path file = Paths.get(RESERVATION_FILE);
        if (!Files.exists(file)) return;

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", -1);

                if (parts.length != 8) {
                    throw new IOException("Invalid reservation data: " + line);
                }

                Person person = personRepository.findById(parts[1]);
                Vehicle vehicle = vehicleRepository.findById(parts[2]);
                ParkingSpace space = parkingSpaceRepository.findById(parts[3]);

                if (person == null || vehicle == null || space == null) {
                    throw new IOException("Reservation contains an invalid reference: " + parts[0]);
                }

                ReservationStatus status = ReservationStatus.valueOf(parts[7]);
                LocalDateTime actualEndTime = parts[6].isEmpty() ? null : LocalDateTime.parse(parts[6]);

                Reservation reservation = new Reservation(
                        parts[0],
                        person,
                        vehicle,
                        space,
                        LocalDateTime.parse(parts[4]),
                        LocalDateTime.parse(parts[5]),
                        actualEndTime,
                        status
                );

                // Restore occupied spaces from active reservations.
                if (status == ReservationStatus.ACTIVE) {
                    if (space.getStatus() == ParkingSpaceStatus.OCCUPIED) {
                        throw new IOException("Multiple active reservations use space: " + space.getSpaceId()); //Because ParkingSpace are created as AVAILABLE:
                    }

                    space.occupy();
                }

                reservationRepository.saveReservation(reservation);
            }
        }
    }
}
