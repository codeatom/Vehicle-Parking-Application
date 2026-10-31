package app.parking.models;

public class Vehicle {
    private final String registrationNumber;
    private final Person owner;

    public Vehicle(String registrationNumber, Person owner) {
        this.registrationNumber = registrationNumber;
        this.owner = owner;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public Person getOwner() {
        return owner;
    }
}