package app.parking.repos;

import app.parking.enums.*;
import app.parking.models.*;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationRepository extends InMemoryRepository<Reservation> {
    public void saveReservation(Reservation reservation) {
        save(reservation.getReservationId(), reservation);
    }

    public List<Reservation> findActiveReservations() {
        return data.values().stream()
                .filter(reservation ->
                        reservation.getStatus() ==
                                ReservationStatus.ACTIVE)
                .collect(Collectors.toList());
    }
}