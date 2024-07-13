package bookMyCar.repositories;

import bookMyCar.entities.Car;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> getAllByAgency_Id(Long hotelId);

    Optional<Car> getRoomByIdAndAgency_Id(Long id, Long hotelId);

    List<Car> getAllByIdNotIn(Set<Long> ids);

    void deleteRoomByAgency_Id(Long hotelId);

    List<Car> getAllByIdNotIn(Set<Long> ids, Sort sort);

    @Query("select r from Car r where r.price >= :min and r.price <= :max "
            + "and r.id not in :ids "
            + "and r.nrGuests >= :nrOfGuests "
            + "and r.agency.location like %:location% "
            + "order by r.price")
    List<Car> getAllByFilters(BigDecimal min,
                              BigDecimal max,
                              Set<Long> ids,
                              String location,
                              Integer nrOfGuests);
}
