package bookMyCar.repositories;

import bookMyCar.entities.RentRequest;
import bookMyCar.entities.Car;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface RequestRepository extends JpaRepository<RentRequest, Long> {

    List<RentRequest> getAllByModerator_Id(Long moderatorId, Sort startDate);

    List<RentRequest> getAllByGuest_Id(Long guestId, Sort startDate);

    @Query("select b.id from RentRequest b where b.startDate >= current_date")
    Set<Long> getActualIds();

    @Query("select b.car.id from RentRequest b where (b.startDate >= current_date) "
            + "and (:start between b.startDate and b.endDate "
            + "or :end between b.startDate and b.endDate "
            + "or b.startDate between :start and :end "
            + "or b.endDate between :start and :end)")
    Set<Long> getActualIds(Timestamp start, Timestamp end);

    void deleteAllByCar(Car car);

    @Query("select b.car.id from RentRequest b where (b.startDate >= current_date) "
            + "and (:start between b.startDate and b.endDate "
            + "or :end between b.startDate and b.endDate "
            + "or b.startDate between :start and :end "
            + "or b.endDate between :start and :end) "
            + "and b.guest.id = :id")
    Collection<Long> getActualIdsByGuest(Timestamp start, Timestamp end, Long id);
}
