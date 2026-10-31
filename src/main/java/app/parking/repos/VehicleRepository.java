package app.parking.repos;

import app.parking.models.Vehicle;

public class VehicleRepository extends InMemoryRepository<Vehicle> {
    public void saveVehicle(Vehicle vehicle) {
        save(vehicle.getRegistrationNumber(), vehicle);
    }
}
