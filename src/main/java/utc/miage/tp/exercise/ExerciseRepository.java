package utc.miage.tp.exercise;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

  boolean existsByName(String name);

  Optional<Exercise> findByName(String name);
}
