package utc.miage.tp.config;

import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import utc.miage.tp.friendship.FriendshipService;
import utc.miage.tp.sport.Sport;
import utc.miage.tp.sport.SportRepository;
import utc.miage.tp.user.PracticeLevel;
import utc.miage.tp.user.Role;
import utc.miage.tp.user.Sex;
import utc.miage.tp.user.User;
import utc.miage.tp.user.UserRepository;
import utc.miage.tp.weather.Weather;
import utc.miage.tp.workout.Workout;
import utc.miage.tp.workout.WorkoutRepository;

@Component
public class ReferenceDataInitializer implements CommandLineRunner {

  private final WorkoutRepository workoutRepository;
  private final UserRepository userRepository;
  private final SportRepository sportRepository;
  private final PasswordEncoder passwordEncoder;
  private final FriendshipService friendshipService;

  public ReferenceDataInitializer(
      UserRepository userRepository,
      SportRepository sportRepository,
      PasswordEncoder passwordEncoder,
      WorkoutRepository workoutRepository,
      FriendshipService friendshipService) {
    this.userRepository = userRepository;
    this.sportRepository = sportRepository;
    this.passwordEncoder = passwordEncoder;
    this.workoutRepository = workoutRepository;
    this.friendshipService = friendshipService;
  }

  @Override
  @Transactional
  public void run(String... args) {
    seedDemoData();
  }

  private void seedDemoData() {
    // Users - Classiques
    User userAlice =
        createUser(
            "Alice Martin",
            "alice.martin@demo.local",
            65.5,
            165.0,
            Sex.FEMALE,
            LocalDate.of(1998, 4, 14),
            PracticeLevel.BEGINNER);
    User userBenoit =
        createUser(
            "Benoit Leroy",
            "benoit.leroy@demo.local",
            75.5,
            180.0,
            Sex.MALE,
            LocalDate.of(1994, 8, 23),
            PracticeLevel.INTERMEDIATE);
    User userOwen =
        createUser(
            "Owen Mercier",
            "owen.mercier@demo.local",
            85.0,
            185.0,
            Sex.MALE,
            LocalDate.of(1990, 1, 5),
            PracticeLevel.ADVANCED);
    User userAdmin =
        createUser(
            "Admin",
            "admin@demo.local",
            70.0,
            175.0,
            Sex.FEMALE,
            LocalDate.of(1992, 6, 2),
            PracticeLevel.INTERMEDIATE);
    userAdmin.setRole(Role.ADMIN);

    // Users - Zootopia
    User userJudy =
        createUser(
            "Judy Hopps",
            "judy.hopps@demo.local",
            38.0,
            102.0,
            Sex.FEMALE,
            LocalDate.of(1999, 3, 12),
            PracticeLevel.INTERMEDIATE);
    User userNick =
        createUser(
            "Nick Wilde",
            "nick.wilde@demo.local",
            72.0,
            168.0,
            Sex.MALE,
            LocalDate.of(1995, 7, 27),
            PracticeLevel.INTERMEDIATE);
    User userBogo =
        createUser(
            "Chief Bogo",
            "chief.bogo@demo.local",
            110.0,
            190.0,
            Sex.MALE,
            LocalDate.of(1986, 11, 8),
            PracticeLevel.ADVANCED);
    User userBellwether =
        createUser(
            "Dawn Bellwether",
            "dawn.bellwether@demo.local",
            55.0,
            150.0,
            Sex.FEMALE,
            LocalDate.of(1997, 9, 30),
            PracticeLevel.BEGINNER);

    // Users - How to Train Your Dragon
    User userHiccup =
        createUser(
            "Hiccup Haddock",
            "hiccup.haddock@demo.local",
            68.0,
            178.0,
            Sex.MALE,
            LocalDate.of(1998, 5, 20),
            PracticeLevel.INTERMEDIATE);
    User userAstrid =
        createUser(
            "Astrid Hofferson",
            "astrid.hofferson@demo.local",
            61.0,
            170.0,
            Sex.FEMALE,
            LocalDate.of(1998, 2, 16),
            PracticeLevel.ADVANCED);
    User userStoick =
        createUser(
            "Stoick the Vast",
            "stoick.vast@demo.local",
            120.0,
            198.0,
            Sex.MALE,
            LocalDate.of(1980, 10, 10),
            PracticeLevel.ADVANCED);
    User userFishlegs =
        createUser(
            "Fishlegs Ingerman",
            "fishlegs.ingerman@demo.local",
            95.0,
            182.0,
            Sex.MALE,
            LocalDate.of(1997, 12, 22),
            PracticeLevel.INTERMEDIATE);

    // Users - Robots
    User userRodney =
        createUser(
            "Rodney Copperbottom",
            "rodney.copperbottom@demo.local",
            78.0,
            176.0,
            Sex.MALE,
            LocalDate.of(1996, 4, 4),
            PracticeLevel.INTERMEDIATE);
    User userCappy =
        createUser(
            "Cappy",
            "cappy@demo.local",
            58.0,
            168.0,
            Sex.FEMALE,
            LocalDate.of(1996, 3, 3),
            PracticeLevel.BEGINNER);
    User userFender =
        createUser(
            "Fender",
            "fender@demo.local",
            72.0,
            174.0,
            Sex.MALE,
            LocalDate.of(1995, 1, 15),
            PracticeLevel.INTERMEDIATE);
    User userBigweld =
        createUser(
            "Bigweld",
            "bigweld@demo.local",
            105.0,
            192.0,
            Sex.MALE,
            LocalDate.of(1984, 7, 9),
            PracticeLevel.ADVANCED);

    userRepository.saveAll(
        List.of(
            userAlice,
            userBenoit,
            userOwen,
            userAdmin,
            userJudy,
            userNick,
            userBogo,
            userBellwether,
            userHiccup,
            userAstrid,
            userStoick,
            userFishlegs,
            userRodney,
            userCappy,
            userFender,
            userBigweld));

    // Friendships
    friendshipService.createAcceptedFriendship(userAlice.getId(), userOwen.getId());
    friendshipService.sendRequest(userBenoit.getId(), userAlice.getId());
    friendshipService.sendRequest(userAdmin.getId(), userJudy.getId());
    friendshipService.createAcceptedFriendship(userJudy.getId(), userNick.getId());
    friendshipService.createAcceptedFriendship(userHiccup.getId(), userAstrid.getId());
    friendshipService.createAcceptedFriendship(userStoick.getId(), userFishlegs.getId());
    friendshipService.createAcceptedFriendship(userRodney.getId(), userCappy.getId());
    friendshipService.createAcceptedFriendship(userFender.getId(), userBigweld.getId());
    friendshipService.createAcceptedFriendship(userJudy.getId(), userAstrid.getId());
    friendshipService.createAcceptedFriendship(userNick.getId(), userRodney.getId());
    friendshipService.createAcceptedFriendship(userBogo.getId(), userStoick.getId());
    friendshipService.sendRequest(userBellwether.getId(), userJudy.getId());
    friendshipService.sendRequest(userCappy.getId(), userAstrid.getId());
    friendshipService.sendRequest(userHiccup.getId(), userRodney.getId());
    friendshipService.sendRequest(userFender.getId(), userNick.getId());
    friendshipService.sendRequest(userBigweld.getId(), userBogo.getId());

    // Sports
    Sport sportCourseCanal = createSport("Course du canal", 9.0);
    Sport sportSprintCote = createSport("Sprint en cote", 11.0);
    Sport sportParcoursAgilite = createSport("Parcours d agilite", 8.0);
    Sport sportFractionneIntense = createSport("Fractionne intense", 12.0);
    Sport sportRenforcementFonctionnel = createSport("Renforcement fonctionnel", 7.0);
    Sport sportEnduranceMixte = createSport("Endurance mixte", 8.0);
    Sport sportParkourUrbain = createSport("Parkour urbain", 10.0);
    Sport sportCircuitCardio = createSport("Circuit cardio", 11.0);
    Sport sportMobiliteActive = createSport("Mobilite active", 4.5);
    Sport sportEscaladeVitesse = createSport("Escalade de vitesse", 10.0);
    Sport sportEscaladeBloc = createSport("Escalade bloc", 8.0);
    Sport sportCoursePied = createSport("Course a pied", 9.5);
    Sport sportNatation = createSport("Natation", 9.0);
    Sport sportSautParachute = createSport("Saut en parachute", 4.0);
    Sport sportPlongee = createSport("Plongee sous-marine", 6.0);
    Sport sportFootball = createSport("Football", 8.5);
    Sport sportCyclisme = createSport("Cyclisme", 8.0);
    Sport sportBasketball = createSport("Basketball", 7.5);
    Sport sportTennis = createSport("Tennis", 7.0);
    Sport sportMusculation = createSport("Musculation", 6.0);
    Sport sportYogaDynamique = createSport("Yoga dynamique", 4.0);
    Sport sportRandonnee = createSport("Randonnee", 5.5);

    sportRepository.saveAll(
        List.of(
            sportCourseCanal,
            sportSprintCote,
            sportParcoursAgilite,
            sportFractionneIntense,
            sportRenforcementFonctionnel,
            sportEnduranceMixte,
            sportParkourUrbain,
            sportCircuitCardio,
            sportMobiliteActive,
            sportEscaladeVitesse,
            sportEscaladeBloc,
            sportCoursePied,
            sportNatation,
            sportSautParachute,
            sportPlongee,
            sportFootball,
            sportCyclisme,
            sportBasketball,
            sportTennis,
            sportMusculation,
            sportYogaDynamique,
            sportRandonnee));

    // Workouts
    workoutRepository.saveAll(
        List.of(
            createWorkout(
                LocalDate.of(2026, 4, 1),
                4.2,
                32.0,
                4,
                new Weather(LocalDate.of(2026, 4, 1), "ensoleille"),
                sportCourseCanal,
                userJudy),
            createWorkout(
                LocalDate.of(2026, 4, 3),
                3.8,
                29.0,
                4,
                new Weather(LocalDate.of(2026, 4, 3), "nuageux"),
                sportParcoursAgilite,
                userJudy),
            createWorkout(LocalDate.of(2026, 4, 6), 5.0, 37.0, 5, null, sportCoursePied, userJudy),
            createWorkout(LocalDate.of(2026, 4, 2), 6.5, 43.0, 4, null, sportSprintCote, userNick),
            createWorkout(LocalDate.of(2026, 4, 5), 2.4, 30.0, 3, null, sportNatation, userNick),
            createWorkout(LocalDate.of(2026, 4, 8), 7.2, 48.0, 5, null, sportCoursePied, userNick),
            createWorkout(
                LocalDate.of(2026, 4, 1), 3.0, 36.0, 4, null, sportCircuitCardio, userBogo),
            createWorkout(
                LocalDate.of(2026, 4, 4),
                4.4,
                38.0,
                4,
                null,
                sportRenforcementFonctionnel,
                userBogo),
            createWorkout(
                LocalDate.of(2026, 4, 9), 2.2, 28.0, 3, null, sportEscaladeVitesse, userBogo),
            createWorkout(
                LocalDate.of(2026, 4, 2), 3.7, 31.0, 3, null, sportMobiliteActive, userBellwether),
            createWorkout(
                LocalDate.of(2026, 4, 7), 2.9, 27.0, 4, null, sportNatation, userBellwether),
            createWorkout(
                LocalDate.of(2026, 4, 10), 4.1, 33.0, 4, null, sportCircuitCardio, userBellwether),
            createWorkout(
                LocalDate.of(2026, 4, 1),
                12.0,
                58.0,
                5,
                new Weather(LocalDate.of(2026, 4, 1), "venteux"),
                sportFractionneIntense,
                userHiccup),
            createWorkout(
                LocalDate.of(2026, 4, 4), 6.0, 45.0, 4, null, sportEnduranceMixte, userHiccup),
            createWorkout(
                LocalDate.of(2026, 4, 11), 8.3, 52.0, 5, null, sportCoursePied, userHiccup),
            createWorkout(LocalDate.of(2026, 4, 3), 5.5, 41.0, 4, null, sportFootball, userAstrid),
            createWorkout(
                LocalDate.of(2026, 4, 7), 4.6, 36.0, 4, null, sportEscaladeVitesse, userAstrid),
            createWorkout(
                LocalDate.of(2026, 4, 12), 7.0, 49.0, 3, null, sportSautParachute, userAstrid),
            createWorkout(
                LocalDate.of(2026, 4, 2), 4.8, 44.0, 4, null, sportMusculation, userStoick),
            createWorkout(LocalDate.of(2026, 4, 6), 3.1, 35.0, 3, null, sportPlongee, userStoick),
            createWorkout(LocalDate.of(2026, 4, 13), 6.2, 50.0, 5, null, sportCyclisme, userStoick),
            createWorkout(
                LocalDate.of(2026, 4, 2), 4.0, 39.0, 4, null, sportBasketball, userFishlegs),
            createWorkout(
                LocalDate.of(2026, 4, 8), 3.3, 33.0, 4, null, sportEscaladeBloc, userFishlegs),
            createWorkout(
                LocalDate.of(2026, 4, 14), 5.9, 47.0, 4, null, sportCoursePied, userFishlegs),
            createWorkout(
                LocalDate.of(2026, 4, 3), 6.1, 42.0, 4, null, sportParkourUrbain, userRodney),
            createWorkout(
                LocalDate.of(2026, 4, 9), 4.9, 37.0, 4, null, sportCircuitCardio, userRodney),
            createWorkout(LocalDate.of(2026, 4, 15), 7.4, 51.0, 5, null, sportCyclisme, userRodney),
            createWorkout(
                LocalDate.of(2026, 4, 4), 3.6, 30.0, 3, null, sportMobiliteActive, userCappy),
            createWorkout(LocalDate.of(2026, 4, 10), 4.2, 34.0, 4, null, sportNatation, userCappy),
            createWorkout(
                LocalDate.of(2026, 4, 16), 5.0, 39.0, 3, null, sportYogaDynamique, userCappy),
            createWorkout(
                LocalDate.of(2026, 4, 5), 5.2, 40.0, 4, null, sportCircuitCardio, userFender),
            createWorkout(
                LocalDate.of(2026, 4, 11), 4.5, 35.0, 4, null, sportParkourUrbain, userFender),
            createWorkout(LocalDate.of(2026, 4, 17), 6.6, 46.0, 4, null, sportPlongee, userFender),
            createWorkout(
                LocalDate.of(2026, 4, 6),
                4.7,
                43.0,
                4,
                null,
                sportRenforcementFonctionnel,
                userBigweld),
            createWorkout(
                LocalDate.of(2026, 4, 12), 5.3, 44.0, 4, null, sportEscaladeBloc, userBigweld),
            createWorkout(
                LocalDate.of(2026, 4, 18), 7.1, 53.0, 5, null, sportRandonnee, userBigweld)));
  }

  private Workout createWorkout(
      LocalDate date,
      Double distance,
      Double duration,
      Integer rating,
      Weather weather,
      Sport sport,
      User user) {
    return new Workout(date, distance, duration, rating, weather, sport, user);
  }

  private Sport createSport(String name, Double calPerMin) {
    return new Sport(name, calPerMin);
  }

  @SuppressWarnings("java:S6437") // Demo seed credential; not used outside local sample data.
  private User createUser(
      String name,
      String email,
      Double weight,
      Double height,
      Sex sex,
      LocalDate birthDate,
      PracticeLevel level) {
    User user = new User(name, email, weight, height, sex, birthDate, level);
    user.setPassword(passwordEncoder.encode("demo123"));
    user.setRole(Role.USER);
    return user;
  }
}
