package bookMyCar.repositories;

import bookMyCar.entities.Agency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, Long> {

    List<Agency> findAllByOwner_Email(String email);
}
