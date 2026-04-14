package utc.miage.tp.workout;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import utc.miage.tp.user.User;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

  boolean existsById(int id);

  Optional<Workout> findById(int id);

  List<Workout> findByUser(User user);

  List<Workout> findByUserAndDateBetween(User user, LocalDateTime start, LocalDateTime end);
}
