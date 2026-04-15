package web.sportflow.workout;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {

  boolean existsById(int id);

  Optional<WorkoutExercise> findById(int id);
}
