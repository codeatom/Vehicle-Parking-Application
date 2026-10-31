package app.parking.repos;

import app.parking.models.Person;

public class PersonRepository extends InMemoryRepository<Person> {
    public void savePerson(Person person) {
        save(person.getPhoneNumber(), person);
    }
}
