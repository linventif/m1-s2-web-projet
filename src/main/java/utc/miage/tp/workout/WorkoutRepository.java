package utc.miage.tp.workout;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

	boolean existsById(int id);

	Optional<Workout> findById(int id);
}
