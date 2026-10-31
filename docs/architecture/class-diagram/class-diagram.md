```mermaid
classDiagram
direction TB

class ParkingArea {
    -String areaName
    -List~ParkingSpace~ parkingSpaces
    +ParkingArea(String areaName, List~ParkingSpace~ parkingSpaces)
    +getAreaName() String
    +getParkingSpaces() List~ParkingSpace~
}

class ParkingSpace {
    -String spaceId
    -ParkingSpaceStatus status
    +ParkingSpace(String spaceId)
    +getSpaceId() String
    +getStatus() ParkingSpaceStatus
    +occupy() void
    +release() void
}

class Person {
    -String name
    -String phoneNumber
    -PersonType personType
    +Person(String name, String phoneNumber, PersonType personType)
    +getName() String
    +getPhoneNumber() String
    +getPersonType() PersonType
}

class Vehicle {
    -String registrationNumber
    -Person owner
    +Vehicle(String registrationNumber, Person owner)
    +getRegistrationNumber() String
    +getOwner() Person
}

class Reservation {
    -String reservationId
    -Person person
    -Vehicle vehicle
    -ParkingSpace parkingSpace
    -LocalDateTime startTime
    -LocalDateTime expectedEndTime
    -LocalDateTime actualEndTime
    -ReservationStatus status
    +Reservation(Person person, Vehicle vehicle, ParkingSpace parkingSpace, int durationHours)
    +getReservationId() String
    +getPerson() Person
    +getVehicle() Vehicle
    +getParkingSpace() ParkingSpace
    +getStartTime() LocalDateTime
    +getExpectedEndTime() LocalDateTime
    +getActualEndTime() LocalDateTime
    +getStatus() ReservationStatus
    +completeReservation() void
}

class PersonType {
    <<enumeration>>
    STUDENT
    STAFF
    VISITOR
}

class ReservationStatus {
    <<enumeration>>
    ACTIVE
    COMPLETED
}

class ParkingSpaceStatus {
    <<enumeration>>
    AVAILABLE
    OCCUPIED
}

class ParkingService {
    -PersonRepository personRepository
    -VehicleRepository vehicleRepository
    -ParkingSpaceRepository parkingSpaceRepository
    -ReservationRepository reservationRepository
    +ParkingService(PersonRepository, VehicleRepository, ParkingSpaceRepository, ReservationRepository)
    +registerPerson(String, String, PersonType, String) Person
    +registerVehicle(String, Person) Vehicle
    +displayParkingSpaces() void
    +getAvailableSpaces() List~ParkingSpace~
    +createReservation(String, String, String, int) Reservation
    +completeReservation(String) void
    +displayActiveReservations() void
}

Person "1" --> "0..*" Vehicle : owns
Person "1" --> "0..*" Reservation : makes
Vehicle "1" --> "0..*" Reservation : used in
ParkingSpace "1" --> "0..*" Reservation : assigned to
ParkingArea "1" *-- "1..*" ParkingSpace : contains