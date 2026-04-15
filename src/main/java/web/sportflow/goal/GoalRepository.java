package web.sportflow.goal;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {

  boolean existsByLabel(String label);

  Optional<Goal> findByLabel(String label);
}
