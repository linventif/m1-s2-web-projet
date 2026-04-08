package utc.miage.tp.config;

import java.time.LocalDateTime;
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
    List<User> users =
        List.of(
            createUser("Alice Martin", "alice.martin@demo.local", 65.5, 165.0, Sex.FEMALE),
            createUser("Benoit Leroy", "benoit.leroy@demo.local", 75.5, 180.0, Sex.MALE),
            createUser("Owen Mercier", "owen.mercier@demo.local", 85.0, 185.0, Sex.MALE));

    userRepository.saveAll(users);

    List<Sport> sports =
        List.of(
            createSport("Climbing - Speed", 3.0),
            createSport("Climbing - Booldering", 2.0),
            createSport("Running", 2.0),
            createSport("Natation", 3.0),
            createSport("Skydiving", 2.0),
            createSport("Diving", 2.0));

    sportRepository.saveAll(sports);

    List<Workout> workouts =
        List.of(
            createWorkout(
                LocalDateTime.of(2026, 3, 31, 10, 0), 1.0, 30.0, sports.get(3), users.get(1)),
            createWorkout(
                LocalDateTime.of(2026, 3, 30, 10, 0), 1.0, 30.0, sports.get(3), users.get(1)),
            createWorkout(
                LocalDateTime.of(2026, 3, 29, 10, 0), 1.0, 30.0, sports.get(3), users.get(1)));

    workoutRepository.saveAll(workouts);
  }

  private Workout createWorkout(
      LocalDateTime date, Double distance, Double duration, Sport sport, User user) {
    return new Workout(date, distance, duration, sport, user);
  }

  private Sport createSport(String name, Double calPerMin) {
    return new Sport(name, calPerMin);
  }

  private User createUser(String name, String email, Double weight, Double height, Sex sex) {
    User user = new User(name, email, weight, height, sex);
    user.setPassword(passwordEncoder.encode("demo123"));
    user.setRole(Role.USER);
    return user;
  }
}
