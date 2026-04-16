package web.sportflow.sport;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SportRepository extends JpaRepository<Sport, Long> {

  boolean existsByName(String name);

  Optional<Sport> findByName(String name);

  @Query("select s.name from Sport s order by s.name asc")
  List<String> findAllNames();
}
