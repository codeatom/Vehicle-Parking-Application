package app.parking;

import app.parking.enums.*;
import app.parking.models.*;
import app.parking.repos.*;
import app.parking.service.*;
import java.util.Scanner;


public class VehicleParkingApplication {
     static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Initialize repositories
        PersonRepository personRepo = new PersonRepository();
        VehicleRepository vehicleRepo = new VehicleRepository();
        ParkingSpaceRepository spaceRepo = new ParkingSpaceRepository();
        ReservationRepository reservationRepo = new ReservationRepository();

        // Initialize ParkingService
        ParkingService parkingService = new ParkingService(personRepo, vehicleRepo, spaceRepo, reservationRepo);

        // Create parking spaces
        int totalSpaces = 20;
        for (int i = 1; i <= totalSpaces; i++) {
            String spaceId = "P" + i;
            spaceRepo.save(spaceId, new ParkingSpace(spaceId)
            );
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
                running = false;
                break;
            }

            String input = scanner.nextLine();

            if (input.matches("\\d{1,2}")) {
                choice = Integer.parseInt(input);
            } else {
                System.out.println("Error: Please enter a valid menu number.");
                continue;
            }

            switch (choice) {
                // REGISTER AND RESERVE PARKING
                case 1:
                    System.out.println("\n--- REGISTRATION ---");

                    String name;
                    while (true) {
                        System.out.print("Enter your name: ");
                        name = scanner.nextLine();

                        if (name.trim().isEmpty()) {
                            System.out.println("Error: Name cannot be empty. Please try again.");
                        } else {
                            break;
                        }
                    }

                    String phone;
                    while (true) {
                        System.out.print("Enter phone number: ");
                        phone = scanner.nextLine();

                        if (!phone.matches("\\+?[0-9]{1,15}")) {
                            System.out.println("Error: Invalid phone number. Please try again.");
                        } else {
                            break;
                        }
                    }

                    String typeInput;
                    while (true) {
                        System.out.println("\nWho are you:");
                        System.out.println("1. Student");
                        System.out.println("2. Staff");
                        System.out.println("3. Visitor");
                        System.out.print("Choose one: ");

                        typeInput = scanner.nextLine();

                        if (!typeInput.matches("[1-3]")) {
                            System.out.println("Error: Invalid person type. Please choose 1, 2, or 3.");
                        } else {
                            break;
                        }
                    }
                    int typeChoice = Integer.parseInt(typeInput);

                    PersonType type = null;
                    switch (typeChoice) {
                        case 1 -> type = PersonType.STUDENT;
                        case 2 -> type = PersonType.STAFF;
                        case 3 -> type = PersonType.VISITOR;
                        default -> {
                            System.out.println("Invalid person type.");
                        }
                    }

                    String registration;
                    while (true) {
                        System.out.print("Enter vehicle registration number: ");
                        registration = scanner.nextLine();

                        if (registration.trim().isEmpty()) {
                            System.out.println("Error: Registration number cannot be empty. Please try again.");
                        } else {
                            break;
                        }
                    }

                    Person person = parkingService.registerPerson(name, phone, type, registration);

                    System.out.println("\nRegistration successful!");
                    System.out.println("Welcome, " + person.getName());

                    parkingService.displayParkingSpaces();

                    System.out.println("\nAvailable spaces: " + parkingService.getAvailableSpaces().size());

                    String spaceId;
                    while (true) {
                        System.out.print("\nEnter parking space ID: ");
                        spaceId = scanner.nextLine();

                        if (spaceId.trim().isEmpty()) {
                            System.out.println("Error: Parking space ID cannot be empty. Please try again.");
                        } else {
                            break;
                        }
                    }

                    int duration;
                    while (true) {
                        System.out.print("How many hours do you plan to park? ");
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

                    Reservation reservation = parkingService.createReservation(phone, registration, spaceId, duration);

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
                    parkingService.displayParkingSpaces();
                    System.out.println("\nAvailable spaces: " + parkingService.getAvailableSpaces().size());
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

                    parkingService.completeReservation(reservationId);
                    break;

                // DISPLAY ACTIVE RESERVATIONS
                case 4:
                    parkingService.displayActiveReservations();
                    break;

                // EXIT APPLICATION
                case 5:
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
