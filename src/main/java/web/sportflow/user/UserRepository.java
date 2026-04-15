package web.sportflow.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);

  List<User> findByIdIn(List<Long> userIds);

  Page<User> findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(
      String firstname, String lastname, Pageable pageable);
}
