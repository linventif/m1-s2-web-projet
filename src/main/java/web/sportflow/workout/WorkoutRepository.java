package web.sportflow.workout;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import web.sportflow.user.User;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

  boolean existsById(int id);

  Optional<Workout> findById(int id);

  List<Workout> findByUser(User user);

  List<Workout> findByUserAndDateBetween(User user, LocalDateTime start, LocalDateTime end);

  List<Workout> findByUserIdInOrderByDateDesc(List<Long> userIds);

  List<Workout> findAllByOrderByDateDesc();
}
