package web.sportflow.challenge;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import web.sportflow.user.User;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

  boolean existsByTitle(String title);

  Optional<Challenge> findByTitle(String title);

  List<Challenge> findByCreatorIdIn(List<Long> userIds);

  List<Challenge> findByCreatorIn(List<User> users);
}
