package web.sportflow.sport;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SportRepository extends JpaRepository<Sport, Long> {

  boolean existsByName(String name);

  Optional<Sport> findByName(String name);
}
