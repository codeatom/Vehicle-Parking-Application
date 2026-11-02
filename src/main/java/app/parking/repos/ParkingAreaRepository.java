package app.parking.repos;

import app.parking.enums.ParkingSpaceStatus;
import app.parking.models.ParkingArea;
import app.parking.models.ParkingSpace;

import java.util.List;
import java.util.stream.Collectors;

public class ParkingAreaRepository extends InMemoryRepository<ParkingArea> {
    public void saveParkingArea(ParkingArea area) {
        save(area.getAreaId(), area);
    }

    public List<ParkingSpace> findAvailableSpaces(String id) {
        return findById(id).getParkingSpaces().stream()
                .filter(space ->
                        space.getStatus() ==
                                ParkingSpaceStatus.AVAILABLE)
                .collect(Collectors.toList());
    }

}
