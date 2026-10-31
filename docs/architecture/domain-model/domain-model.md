```mermaid
classDiagram
direction TB

class Person {
  -String name
  -String phoneNumber
  -PersonType personType
}

class Vehicle {
  -String registrationNumber
  -Person owner
}

class Reservation {
  -String reservationId
  -LocalDateTime startTime
  -LocalDateTime expectedEndTime
  -LocalDateTime actualEndTime
  -ReservationStatus status
  +completeReservation()
}

class ParkingSpace {
  -String spaceId
  -ParkingSpaceStatus status
  +occupy()
  +release()
}

class ParkingArea {
  -String areaName
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

Person "1" --> "0..*" Vehicle : owns
Person "1" --> "0..*" Reservation : makes
Vehicle "1" --> "0..*" Reservation : used in
ParkingSpace "1" --> "0..*" Reservation : assigned to
ParkingArea "1" *-- "1..*" ParkingSpace : contains