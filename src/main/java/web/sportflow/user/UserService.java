package web.sportflow.user;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  public Page<User> getAll(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  @Transactional(readOnly = true)
  public Optional<User> getUserById(Long id) {
    return userRepository
        .findById(id)
        .map(
            user -> {
              // Initialize lazy collections needed by Thymeleaf views.
              user.getBadges().size();
              user.getGoals().size();
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
    newUser.setProfileImagePath(null);
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

  private String normalizeText(String value) {
    if (value == null) {
      return "";
    }
    return value.trim();
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
    user.setSex(registrationDTO.sex());
    user.setWeight(registrationDTO.weight());
    user.setHeight(registrationDTO.height());
    user.setPassword(registrationDTO.password());
    user.setProfileImagePath(null);
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

  @Transactional
  public User updateCurrentUserProfile(
      User currentUser,
      String firstname,
      String lastname,
      String email,
      Double weight,
      Double height,
      Sex sex,
      LocalDate birthDate,
      PracticeLevel level) {
    if (currentUser == null || currentUser.getId() == null) {
      throw new IllegalArgumentException("Utilisateur non authentifie.");
    }

    String normalizedFirstname = normalizeText(firstname);
    String normalizedLastname = normalizeText(lastname);
    String normalizedEmail = normalizeEmail(email);

    if (normalizedFirstname.isBlank()) {
      throw new IllegalArgumentException("Le prenom est obligatoire.");
    }
    if (normalizedLastname.isBlank()) {
      throw new IllegalArgumentException("Le nom est obligatoire.");
    }
    if (normalizedEmail.isBlank()) {
      throw new IllegalArgumentException("L'email est obligatoire.");
    }
    if (weight == null || weight <= 0) {
      throw new IllegalArgumentException("Le poids doit etre superieur a 0.");
    }
    if (height == null || height <= 0) {
      throw new IllegalArgumentException("La taille doit etre superieure a 0.");
    }
    if (sex == null) {
      throw new IllegalArgumentException("Le sexe est obligatoire.");
    }
    if (level == null) {
      throw new IllegalArgumentException("Le niveau est obligatoire.");
    }
    if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("La date de naissance ne peut pas etre dans le futur.");
    }

    Optional<User> existingUserByEmail = userRepository.findByEmail(normalizedEmail);
    if (existingUserByEmail.isPresent()
        && !existingUserByEmail.get().getId().equals(currentUser.getId())) {
      throw new IllegalArgumentException("Un utilisateur avec cet email existe deja.");
    }

    currentUser.setFirstname(normalizedFirstname);
    currentUser.setLastname(normalizedLastname);
    currentUser.setEmail(normalizedEmail);
    currentUser.setWeight(weight);
    currentUser.setHeight(height);
    currentUser.setSex(sex);
    currentUser.setBirthDate(birthDate);
    currentUser.setLevel(level);

    return userRepository.save(currentUser);
  }

  public double calculateBMI(User user) {
    if (user.getHeight() == null || user.getWeight() == null) return 0;
    double heightMeters = user.getHeight() / 100.0;
    double bmi = user.getWeight() / (heightMeters * heightMeters);
    return Math.round(bmi * 10.0) / 10.0;
  }

  public double calculateBMR(User user) {
    if (user.getHeight() == null
        || user.getWeight() == null
        || user.getBirthDate() == null
        || user.getSex() == null) return 0;
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
    else
      return "Entraînement axé sur la perte de poids avec cardio et exercices de haute intensité.";
  }

  public Page<User> searchUsers(String searchString, Pageable pageable) {
    return userRepository.findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(
        searchString, searchString, pageable);
  }
}
