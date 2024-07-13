package bookMyCar.repositories;

import bookMyCar.entities.Rent;
import bookMyCar.entities.Car;
import bookMyCar.entities.Status;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public interface RentRepository extends JpaRepository<Rent, Long> {

    List<Rent> getAllByStatusAndModerator_Id(Status status, Long moderatorId, Sort sort);

    List<Rent> getAllByStatusAndGuest_Id(Status status, Long guestId, Sort sort);

    @Query("select b.id from Rent b where b.startDate >= current_date and b.status = 'ACCEPTED'")
    Set<Long> getActualIds();

    @Query("select b.car.id from Rent b where (b.startDate >= current_date and b.status = 'ACCEPTED') "
            + "and (:start between b.startDate and b.endDate "
            + "or :end between b.startDate and b.endDate "
            + "or b.startDate between :start and :end "
            + "or b.endDate between :start and :end)")
    Set<Long> getActualIds(Timestamp start, Timestamp end);

    void deleteAllByCar(Car car);

    @Query("select b.car.id from Rent b where (b.startDate >= current_date and b.status = 'ACCEPTED') "
            + "and (:start between b.startDate and b.endDate "
            + "or :end between b.startDate and b.endDate "
            + "or b.startDate between :start and :end "
            + "or b.endDate between :start and :end) "
            + "and b.guest.id = :id")
    Set<Long> getActualIdsByGuest(Timestamp start, Timestamp end, Long id);
}
