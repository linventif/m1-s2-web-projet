package web.sportflow.goal;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import web.sportflow.user.User;

public interface GoalRepository extends JpaRepository<Goal, Long> {

  boolean existsByLabel(String label);

  Optional<Goal> findByLabel(String label);

  List<Goal> findByUserIn(List<User> users);
}
