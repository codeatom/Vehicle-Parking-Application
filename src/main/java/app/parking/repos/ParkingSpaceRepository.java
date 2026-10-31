package app.parking.repos;

import app.parking.enums.*;
import app.parking.models.*;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingSpaceRepository extends InMemoryRepository<ParkingSpace> {
    public void saveParkingSpace(ParkingSpace parkingSpace) {
        save(parkingSpace.getSpaceId(), parkingSpace);
    }

    public List<ParkingSpace> findAvailableSpaces() {
        return data.values().stream()
                .filter(space ->
                        space.getStatus() ==
                                ParkingSpaceStatus.AVAILABLE)
                .collect(Collectors.toList());
    }
}