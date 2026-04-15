package web.sportflow.challenge;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

  boolean existsByTitle(String title);

  Optional<Challenge> findByTitle(String title);
}
