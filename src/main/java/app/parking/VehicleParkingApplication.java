package app.parking;

import app.parking.enums.*;
import app.parking.models.*;
import app.parking.repos.*;
import app.parking.service.*;
import app.parking.storage.FileStorageService;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;


public class VehicleParkingApplication {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Initialize repositories
        PersonRepository personRepo = new PersonRepository();
        VehicleRepository vehicleRepo = new VehicleRepository();
        ParkingSpaceRepository spaceRepo = new ParkingSpaceRepository();
        ReservationRepository reservationRepo = new ReservationRepository();
        ParkingAreaRepository parkingAreaRepo = new ParkingAreaRepository();

        // Initialize ParkingService
        ParkingService parkingService = new ParkingService(
                personRepo,
                vehicleRepo,
                spaceRepo,
                reservationRepo,
                parkingAreaRepo
        );

        // Initialize FileStorageService
        FileStorageService storageService = new FileStorageService(
                personRepo,
                vehicleRepo,
                spaceRepo,
                reservationRepo,
                parkingAreaRepo
        );

        // Load saved information
        try {
            storageService.initialize();
            System.out.println("Data loaded successfully.");

        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
            scanner.close();
            return;
        }

        // Find existing parking area
        ParkingArea parkingArea = parkingAreaRepo.findById("Area1");

        // Create area only if it does not exist
        if (parkingArea == null) {

            parkingArea = parkingService.initParkingAreaAndSpaces(
                    "Area1",
                    "Area 1",
                    10
            );

            try {
                storageService.saveAll();
                System.out.println("New parking area created and saved.");

            } catch (IOException e) {
                System.out.println("Error saving parking area: " + e.getMessage());
            }

        } else {
            System.out.println("Existing parking area loaded.");
        }

        boolean running = true;

        while (running) {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Register and Reserve Parking");
            System.out.println("2. Display Parking Spaces");
            System.out.println("3. Complete Reservation");
            System.out.println("4. Display Active Reservations");
            System.out.println("5. Exit");
            System.out.println("==============================");

            System.out.print("Choose an option: ");
            int choice;

            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine();

            if (input.matches("\\d{1}")) {
                choice = Integer.parseInt(input);
            } else {
                System.out.println("Error: Please enter a valid menu number.");
                continue;
            }

            switch (choice) {
                // REGISTER AND RESERVE PARKING
                case 1:
                    System.out.println("\n---- REGISTRATION ----");

                    parkingService.displayParkingSpaces(parkingArea.getAreaId());
                    System.out.println("\nAvailable spaces: " + parkingService.getAvailableSpaces(parkingArea.getAreaId()).size());

                    String spaceId;
                    List<ParkingSpace> spaces = parkingArea.getParkingSpaces();

                    while (true) {
                        System.out.print("Choose a parking space ID: ");
                        spaceId = scanner.nextLine().trim();

                        if (spaceId.isEmpty()) {
                            System.out.println("Error: Parking space ID cannot be empty. Please try again.");
                            continue;
                        }

                        boolean found = false;

                        for (ParkingSpace p : spaces) {
                            if (spaceId.equalsIgnoreCase(p.getSpaceId()) && p.getStatus().equals(ParkingSpaceStatus.AVAILABLE) ) {
                                found = true;
                                break;
                            }
                        }

                        if (found) {
                            break;
                        } else {
                            System.out.println("Error: Wrong choice. Please try again. Choose a space that is available.\n");
                        }
                    }

                    int duration;
                    while (true) {
                        System.out.print("\nHow many hours do you plan to park? ");
                        String durationInput = scanner.nextLine();

                        if (!durationInput.matches("[0-9]+")) {
                            System.out.println("Error: Duration must be a number. Please try again.");
                        } else {
                            duration = Integer.parseInt(durationInput);

                            if (duration <= 0 || duration > 24) {
                                System.out.println("Error: Duration must be between 1 and 24 hours. Please try again.");
                            } else {
                                break;
                            }
                        }
                    }

                    String phoneNumber;
                    Person existingPerson;

                    while (true) {
                        System.out.print("Enter phone number: ");
                        phoneNumber = scanner.nextLine().trim();

                        if (phoneNumber.isEmpty()) {
                            System.out.println("Error: Phone number cannot be empty.");
                            continue;
                        }

                        if (!phoneNumber.matches("\\+?[0-9]{1,15}")) {
                            System.out.println("Error: Invalid phone number.");
                            continue;
                        }

                        existingPerson = personRepo.findById(phoneNumber);

                        if (existingPerson != null) {
                            System.out.println("Welcome back, " + existingPerson.getName());
                        } else {
                            System.out.println("New customer. Welcome!");
                        }

                        break;
                    }

                    String name;
                    PersonType type;

                    if (existingPerson != null) {
                        name = existingPerson.getName();
                        type = existingPerson.getPersonType();
                    } else {
                        // Ask for name
                        while (true) {
                            System.out.print("\nEnter your name: ");
                            name = scanner.nextLine().trim();

                            if (name.isEmpty()) {
                                System.out.println("Error: Name cannot be empty.");
                            } else {
                                break;
                            }
                        }

                        // Ask for person type
                        while (true) {
                            System.out.println("\nAre you a:");
                            System.out.println("1. Student");
                            System.out.println("2. Staff");
                            System.out.println("3. Visitor");
                            System.out.print("Choose one: ");

                            String typeInput = scanner.nextLine();

                            if (!typeInput.matches("[1-3]")) {
                                System.out.println("Error: Choose 1, 2, or 3.");
                                continue;
                            }

                            switch (typeInput) {
                                case "1" -> type = PersonType.STUDENT;
                                case "2" -> type = PersonType.STAFF;
                                case "3" -> type = PersonType.VISITOR;
                                default -> throw new IllegalStateException();
                            }

                            break;
                        }
                    }

                    String registration;
                    Vehicle existingVehicle;

                    while (true) {
                        System.out.print("\nEnter vehicle registration number: ");
                        registration = scanner.nextLine().trim().toUpperCase();

                        if (registration.isEmpty()) {
                            System.out.println("Error: Registration cannot be empty.");
                            continue;
                        }

                        if (!registration.matches("[a-zA-Z0-9]+")) {
                            System.out.println("Error: Invalid registration.");
                            continue;
                        }

                        existingVehicle = vehicleRepo.findById(registration);

                        if (existingVehicle != null &&
                                !existingVehicle.getOwner().getPhoneNumber().equals(phoneNumber)) {

                            System.out.println("Error: This vehicle belongs to another customer.");
                            continue;
                        }

                        break;
                    }

                    //Create registration
                    Reservation reservation = parkingService.createReservation(name, type, phoneNumber,
                            registration, parkingArea.getAreaId(), spaceId, duration
                    );

                    try {
                        storageService.saveAll();
                        System.out.println("Reservation data saved successfully.");
                    } catch (IOException e) {
                        System.out.println("Error saving data: " + e.getMessage());
                    }

                    System.out.println("\n===== RESERVATION CONFIRMED =====");
                    System.out.println("Reservation ID: " + reservation.getReservationId());
                    System.out.println("Name: " + reservation.getPerson().getName());
                    System.out.println("Vehicle: " + reservation.getVehicle().getRegistrationNumber());
                    System.out.println("Parking Space: " + reservation.getParkingSpace().getSpaceId());
                    System.out.println("Start Time: " + reservation.getStartTime());
                    System.out.println("Expected End Time: " + reservation.getExpectedEndTime());
                    System.out.println("Status: " + reservation.getStatus());
                    System.out.println("=================================");
                    break;

                // DISPLAY PARKING SPACES
                case 2:
                    parkingService.displayParkingSpaces("Area1");
                    System.out.println("\nAvailable spaces: " + parkingService.getAvailableSpaces("Area1").size());
                    break;

                // COMPLETE RESERVATION
                case 3:
                    System.out.println("\n--- COMPLETE RESERVATION ---");

                    String reservationId;
                    while (true) {
                        System.out.print("Enter reservation ID: ");
                        reservationId = scanner.nextLine();

                        if (reservationId.trim().isEmpty()) {
                            System.out.println("Error: Reservation ID cannot be empty. Please try again.");
                        } else {
                            break;
                        }
                    }

                    try {
                        parkingService.completeReservation(reservationId);

                        storageService.saveAll();

                        System.out.println("Changes saved successfully.");

                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());

                    } catch (IOException e) {
                        System.out.println("Error saving data: " + e.getMessage());
                    }
                    break;

                // DISPLAY ACTIVE RESERVATIONS
                case 4:
                    parkingService.displayActiveReservations();
                    break;

                // EXIT APPLICATION
                case 5:
                    try {
                        storageService.saveAll();
                        System.out.println("All data saved successfully.");
                    } catch (IOException e) {
                        System.out.println("Error saving data: " + e.getMessage());
                    }

                    running = false;
                    System.out.println("\nThank you for using our Parking System!");
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
    }
}
