package app.parking.models;

import app.parking.enums.PersonType;

public class Person {

    private final String name;
    private final String phoneNumber;
    private final PersonType personType;

    public Person(String name, String phoneNumber, PersonType personType) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.personType = personType;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public PersonType getPersonType() {
        return personType;
    }
}
