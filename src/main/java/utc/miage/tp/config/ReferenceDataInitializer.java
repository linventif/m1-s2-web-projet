package utc.miage.tp.config;

import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import utc.miage.tp.sport.Sport;
import utc.miage.tp.sport.SportRepository;
import utc.miage.tp.user.Role;
import utc.miage.tp.user.Sex;
import utc.miage.tp.user.User;
import utc.miage.tp.user.UserRepository;
import utc.miage.tp.workout.Workout;
import utc.miage.tp.workout.WorkoutRepository;

@Component
public class ReferenceDataInitializer implements CommandLineRunner {

  private final WorkoutRepository workoutRepository;
  private final UserRepository userRepository;
  private final SportRepository sportRepository;
  private final PasswordEncoder passwordEncoder;

  public ReferenceDataInitializer(
      UserRepository userRepository,
      SportRepository sportRepository,
      PasswordEncoder passwordEncoder,
      WorkoutRepository workoutRepository) {
    this.userRepository = userRepository;
    this.sportRepository = sportRepository;
    this.passwordEncoder = passwordEncoder;
    this.workoutRepository = workoutRepository;
  }

  @Override
  @Transactional
  public void run(String... args) {
    seedDemoData();
  }

  private void seedDemoData() {
    // Users
    User userAlice = createUser("Alice Martin", "alice.martin@demo.local", 65.5, 165.0, Sex.FEMALE);
    User userBenoit = createUser("Benoit Leroy", "benoit.leroy@demo.local", 75.5, 180.0, Sex.MALE);
    User userOwen = createUser("Owen Mercier", "owen.mercier@demo.local", 85.0, 185.0, Sex.MALE);
    userRepository.saveAll(List.of(
        userAlice,
        userBenoit,
        userOwen));

    // Friends
    userAlice.addFriends(userOwen);
    userRepository.save(userAlice);

    // Sports
    Sport sportClimbingSpeed = createSport("Climbing - Speed", 3.0);
    Sport sportClimbingBooldering = createSport("Climbing - Booldering", 2.0);
    Sport sportRunning = createSport("Running", 2.0);
    Sport sportNatation = createSport("Natation", 3.0);
    Sport sportSkydiving = createSport("Skydiving", 2.0);
    Sport sportDiving = createSport("Diving", 2.0);
    sportRepository.saveAll(List.of(
        sportClimbingSpeed,
        sportClimbingBooldering,
        sportRunning,
        sportNatation,
        sportSkydiving,
        sportDiving));

    // Workouts
    Workout workout1 = createWorkout(LocalDate.of(2026, 3, 31), 1.0, 30.0, sportNatation, userBenoit);
    Workout workout2 = createWorkout(LocalDate.of(2026, 3, 30), 1.0, 30.0, sportNatation, userBenoit);
    Workout workout3 = createWorkout(LocalDate.of(2026, 3, 29), 1.0, 30.0, sportNatation, userBenoit);
    workoutRepository.saveAll(List.of(
        workout1,
        workout2,
        workout3));
  }

  private Workout createWorkout(
      LocalDate date, Double distance, Double duration, Sport sport, User user) {
    return new Workout(date, distance, duration, sport, user);
  }

  private Sport createSport(String name, Double calPerMin) {
    return new Sport(name, calPerMin);
  }

  @SuppressWarnings("java:S6437") // Demo seed credential; not used outside local sample data.
  private User createUser(String name, String email, Double weight, Double height, Sex sex) {
    User user = new User(name, email, weight, height, sex);
    user.setPassword(passwordEncoder.encode("demo123"));
    user.setRole(Role.USER);
    return user;
  }
}
