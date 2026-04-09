package utc.miage.tp.user;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

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
            user.getFirstname(),
            user.getLastname(),
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

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));
  }

  public List<User> findAllUsers() {
    return userRepository.findAll();
  }

  public User findById(Long id) {
    return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
  }

  public User registerUser(RegistrationDTO registrationDTO) {
    return registerUser(registrationDTO, false);
  }

  /** Master registration method. */
  public User registerUser(RegistrationDTO registrationDTO, boolean isAdmin) {
    // 1. Encode the password
    User user = new User();
    user.setFirstname(registrationDTO.firstname());
    user.setLastname(registrationDTO.lastname());
    user.setEmail(registrationDTO.email());
    user.setPassword(registrationDTO.password());
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    // 2. Assign the Role
    if (isAdmin) {
      user.setRole(Role.ADMIN);
    } else {
      user.setRole(Role.USER);
    }

    return userRepository.save(user);
  }

    public void save(User user) {
      userRepository.save(user);
    }

    public double calculateBMI(User user) {
      if (user.getHeight() == null || user.getWeight() == null) return 0;
      double heightMeters = user.getHeight() / 100.0;
      double bmi = user.getWeight() / (heightMeters * heightMeters);
      return Math.round(bmi * 10.0) / 10.0;
    }

    public double calculateBMR(User user) {
      if (user.getHeight() == null || user.getWeight() == null || user.getBirthDate() == null || user.getSex() == null) return 0;
      int age = java.time.Period.between(user.getBirthDate(), java.time.LocalDate.now()).getYears();
      double bmr;
      if (user.getSex() == Sex.MALE) {
        bmr = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * age + 5;
      } else {
        bmr = 10 * user.getWeight() + 6.25 * user.getHeight() - 5 * age - 161;
      }
      return Math.round(bmr);
    }

    public String getWorkoutRecommendation(User user) {
      double bmi = calculateBMI(user);
      if (bmi < 18.5) return "Entraînement de renforcement musculaire et prise de masse.";
      else if (bmi < 25) return "Entraînement équilibré avec cardio et musculation.";
      else return "Entraînement axé sur la perte de poids avec cardio et exercices de haute intensité.";
    }
}
