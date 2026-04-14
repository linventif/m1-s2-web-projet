package web.sportflow.badge;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

  boolean existsByName(String name);

  Optional<Badge> findByName(String name);
}
