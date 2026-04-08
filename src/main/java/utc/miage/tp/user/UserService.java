package utc.miage.tp.user;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional(readOnly = true)
  public List<User> getAll() {
    List<User> users = userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

    return users;
  }

  @Transactional(readOnly = true)
  public Optional<User> getUserById(Long id) {
    return userRepository
        .findById(id)
        .map(
            user -> {
              return user;
            });
  }

  @Transactional
  public User createUser(
      User user,
      String rawPassword,
      String codeStatut,
      Collection<Long> organizedConferenceIds,
      Collection<Long> participatingConferenceIds) {
    String normalizedEmail = normalizeEmail(user.getEmail());
    if (userRepository.existsByEmail(normalizedEmail)) {
      throw new IllegalArgumentException("Un utilisateur avec cet email existe deja.");
    }

    User newUser =
        new User(
            user.getName(),
            normalizedEmail,
            user.getWeight(),
            user.getHeight(),
            user.getSex(),
            user.getBirthDate(),
            user.getLevel());
    newUser.setPassword(passwordEncoder.encode(rawPassword));

    User savedUser = userRepository.save(newUser);

    return userRepository.save(savedUser);
  }

  @Transactional(readOnly = true)
  public Optional<User> authenticate(String email, String rawPassword) {
    String normalizedEmail = normalizeEmail(email);
    return userRepository
        .findByEmail(normalizedEmail)
        .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
        .map(
            user -> {
              return user;
            });
  }

  private String normalizeEmail(String email) {
    if (email == null) {
      return "";
    }
    return email.trim().toLowerCase();
  }
}
