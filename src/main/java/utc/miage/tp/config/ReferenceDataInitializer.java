package utc.miage.tp.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import utc.miage.tp.badge.Badge;
import utc.miage.tp.badge.BadgeRepository;
import utc.miage.tp.challenge.Challenge;
import utc.miage.tp.challenge.ChallengeRepository;
import utc.miage.tp.challenge.ChallengeType;
import utc.miage.tp.friendship.FriendshipService;
import utc.miage.tp.goal.Goal;
import utc.miage.tp.goal.GoalRepository;
import utc.miage.tp.goal.GoalType;
import utc.miage.tp.sport.Sport;
import utc.miage.tp.sport.SportRepository;
import utc.miage.tp.user.PracticeLevel;
import utc.miage.tp.user.Role;
import utc.miage.tp.user.Sex;
import utc.miage.tp.user.User;
import utc.miage.tp.user.UserRepository;
import utc.miage.tp.weather.WeatherStatsDTO;
import utc.miage.tp.workout.Workout;
import utc.miage.tp.workout.WorkoutRepository;

@Component
public class ReferenceDataInitializer implements CommandLineRunner {

  private final WorkoutRepository workoutRepository;
  private final UserRepository userRepository;
  private final SportRepository sportRepository;
  private final BadgeRepository badgeRepository;
  private final ChallengeRepository challengeRepository;
  private final GoalRepository goalRepository;
  private final PasswordEncoder passwordEncoder;
  private final FriendshipService friendshipService;

  @Value("${app.avatar-upload-dir:avatar_upload}")
  private String avatarUploadDir;

  public ReferenceDataInitializer(
      UserRepository userRepository,
      SportRepository sportRepository,
      BadgeRepository badgeRepository,
      ChallengeRepository challengeRepository,
      GoalRepository goalRepository,
      PasswordEncoder passwordEncoder,
      WorkoutRepository workoutRepository,
      FriendshipService friendshipService) {
    this.userRepository = userRepository;
    this.sportRepository = sportRepository;
    this.badgeRepository = badgeRepository;
    this.challengeRepository = challengeRepository;
    this.goalRepository = goalRepository;
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
            "Alice",
            "Martin",
            "alice.martin@demo.local",
            65.5,
            165.0,
            Sex.FEMALE,
            LocalDate.of(1998, 4, 14),
            PracticeLevel.BEGINNER);
    User userBenoit =
        createUser(
            "Benoit",
            "Leroy",
            "benoit.leroy@demo.local",
            75.5,
            180.0,
            Sex.MALE,
            LocalDate.of(1994, 8, 23),
            PracticeLevel.INTERMEDIATE);
    User userOwen =
        createUser(
            "Owen",
            "Mercier",
            "owen.mercier@demo.local",
            85.0,
            185.0,
            Sex.MALE,
            LocalDate.of(1990, 1, 5),
            PracticeLevel.ADVANCED);
    User userAdmin =
        createUser(
            "Admin",
            "BG",
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
            "Judy",
            "Hopps",
            "judy.hopps@demo.local",
            38.0,
            102.0,
            Sex.FEMALE,
            LocalDate.of(1999, 3, 12),
            PracticeLevel.INTERMEDIATE);
    User userNick =
        createUser(
            "Nick",
            "Wilde",
            "nick.wilde@demo.local",
            72.0,
            168.0,
            Sex.MALE,
            LocalDate.of(1995, 7, 27),
            PracticeLevel.INTERMEDIATE);
    User userBogo =
        createUser(
            "Chief",
            "Bogo",
            "chief.bogo@demo.local",
            110.0,
            190.0,
            Sex.MALE,
            LocalDate.of(1986, 11, 8),
            PracticeLevel.ADVANCED);
    User userBellwether =
        createUser(
            "Dawn",
            "Bellwether",
            "dawn.bellwether@demo.local",
            55.0,
            150.0,
            Sex.FEMALE,
            LocalDate.of(1997, 9, 30),
            PracticeLevel.BEGINNER);

    // Users - How to Train Your Dragon
    User userHiccup =
        createUser(
            "Hiccup",
            "Haddock",
            "hiccup.haddock@demo.local",
            68.0,
            178.0,
            Sex.MALE,
            LocalDate.of(1998, 5, 20),
            PracticeLevel.INTERMEDIATE);
    User userAstrid =
        createUser(
            "Astrid",
            "Hofferson",
            "astrid.hofferson@demo.local",
            61.0,
            170.0,
            Sex.FEMALE,
            LocalDate.of(1998, 2, 16),
            PracticeLevel.ADVANCED);
    User userStoick =
        createUser(
            "Stoick",
            "the Vast",
            "stoick.vast@demo.local",
            120.0,
            198.0,
            Sex.MALE,
            LocalDate.of(1980, 10, 10),
            PracticeLevel.ADVANCED);
    User userFishlegs =
        createUser(
            "Fishlegs",
            "Ingerman",
            "fishlegs.ingerman@demo.local",
            95.0,
            182.0,
            Sex.MALE,
            LocalDate.of(1997, 12, 22),
            PracticeLevel.INTERMEDIATE);

    // Users - Robots
    User userRodney =
        createUser(
            "Rodney",
            "Copperbottom",
            "rodney.copperbottom@demo.local",
            78.0,
            176.0,
            Sex.MALE,
            LocalDate.of(1996, 4, 4),
            PracticeLevel.INTERMEDIATE);
    User userCappy =
        createUser(
            "Cappy",
            "Barra",
            "cappy@demo.local",
            58.0,
            168.0,
            Sex.FEMALE,
            LocalDate.of(1996, 3, 3),
            PracticeLevel.BEGINNER);
    User userFender =
        createUser(
            "Fender",
            "Def",
            "fender@demo.local",
            72.0,
            174.0,
            Sex.MALE,
            LocalDate.of(1995, 1, 15),
            PracticeLevel.INTERMEDIATE);
    User userBigweld =
        createUser(
            "Bigweld",
            "Bold",
            "bigweld@demo.local",
            105.0,
            192.0,
            Sex.MALE,
            LocalDate.of(1984, 7, 9),
            PracticeLevel.ADVANCED);

    // Users - Kung Fu Panda
    User userShifu =
        createUser(
            "Maitre Shifu",
            "Me",
            "shifu@demo.local",
            56.0,
            152.0,
            Sex.MALE,
            LocalDate.of(1982, 1, 12),
            PracticeLevel.ADVANCED);
    User userOogway =
        createUser(
            "Maitre Oogway",
            "Away",
            "oogway@demo.local",
            73.0,
            168.0,
            Sex.MALE,
            LocalDate.of(1970, 5, 9),
            PracticeLevel.ADVANCED);
    User userPo =
        createUser(
            "Po Ping",
            "Pong",
            "po.ping@demo.local",
            120.0,
            182.0,
            Sex.MALE,
            LocalDate.of(1996, 11, 4),
            PracticeLevel.INTERMEDIATE);
    User userTaiLung =
        createUser(
            "Tai Lung",
            "Shi",
            "tai.lung@demo.local",
            98.0,
            188.0,
            Sex.MALE,
            LocalDate.of(1991, 2, 17),
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
            userBigweld,
            userShifu,
            userOogway,
            userPo,
            userTaiLung));
    assignDemoAvatars(
        Map.ofEntries(
            Map.entry(userJudy, "judy_hopps.png"),
            Map.entry(userNick, "nick_wilde.png"),
            Map.entry(userBogo, "chief_bogo.png"),
            Map.entry(userBellwether, "dawn_bellwether.png"),
            Map.entry(userHiccup, "hiccup_haddock.png"),
            Map.entry(userAstrid, "astrid_hofferson.png"),
            Map.entry(userStoick, "stoick_the_vast.png"),
            Map.entry(userFishlegs, "fishlegs_ingerman.png"),
            Map.entry(userRodney, "rodney_copperbottom.png"),
            Map.entry(userCappy, "cappy_barra.png"),
            Map.entry(userFender, "fender_def.png"),
            Map.entry(userBigweld, "bigweld_bold.png"),
            Map.entry(userShifu, "maitre_shifu_me.png"),
            Map.entry(userOogway, "maitre_oogway_away.png"),
            Map.entry(userPo, "po_ping_pong.png"),
            Map.entry(userTaiLung, "tai_lung_shi.png")));

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
    friendshipService.createAcceptedFriendship(userShifu.getId(), userPo.getId());
    friendshipService.createAcceptedFriendship(userOogway.getId(), userShifu.getId());
    friendshipService.sendRequest(userTaiLung.getId(), userShifu.getId());

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

    // Badges (axes sur certains sports)
    List<Badge> demoBadges =
        badgeRepository.saveAll(
            List.of(
                createSportBadge(
                    sportCoursePied,
                    "Rookie 5K",
                    "Reussir 5 km cumules en " + sportCoursePied.getName() + ".",
                    "/images/badge/running_5km.png"),
                createSportBadge(
                    sportCoursePied,
                    "Marathonien en Herbe",
                    "Cumuler 42 km en " + sportCoursePied.getName() + ".",
                    "/images/badge/running_42km.png"),
                createSportBadge(
                    sportCoursePied,
                    "Marathonien",
                    "Cumuler 42 km en " + sportCoursePied.getName() + " en moins de 4h.",
                    "/images/badge/running_42km.png"),
                createSportBadge(
                    sportNatation,
                    "Ondes Maitrisees",
                    "Completer 3 seances de " + sportNatation.getName() + " en une semaine.",
                    "/images/badge/natation.png"),
                createSportBadge(
                    sportCyclisme,
                    "Rouleur Urbain",
                    "Atteindre 30 km en " + sportCyclisme.getName() + ".",
                    "/images/badge/cyclisme.png"),
                createSportBadge(
                    sportEscaladeBloc,
                    "Bloc Determination",
                    "Valider 5 sessions de " + sportEscaladeBloc.getName() + ".",
                    "/images/badge/escalade.png"),
                createSportBadge(
                    sportYogaDynamique,
                    "Souplesse Focus",
                    "Maintenir 20 minutes de "
                        + sportYogaDynamique.getName()
                        + " sans interruption.",
                    "/images/badge/yoga.png")));
    Badge badgeRookie5k = demoBadges.get(0);
    Badge badgeMarathonHerbe = demoBadges.get(1);
    Badge badgeMarathonien = demoBadges.get(2);
    Badge badgeNatation = demoBadges.get(3);
    Badge badgeCyclisme = demoBadges.get(4);
    Badge badgeEscalade = demoBadges.get(5);
    Badge badgeYoga = demoBadges.get(6);

    LocalDate today = LocalDate.now();
    // Challenges (axes sur certains sports)
    Challenge challengeRunning25k =
        createSportChallenge(
            sportCoursePied,
            "Challenge Running 25K",
            "Cumuler 25 km en " + sportCoursePied.getName() + " sur la periode.",
            ChallengeType.DISTANCE,
            25.0,
            today.minusDays(5),
            today.plusDays(21),
            userAlice);
    challengeRunning25k.getBadges().addAll(List.of(badgeRookie5k, badgeMarathonHerbe));

    Challenge challengeNatationEndurance =
        createSportChallenge(
            sportNatation,
            "Challenge Natation Endurance",
            "Cumuler 180 minutes de " + sportNatation.getName() + ".",
            ChallengeType.DUREE,
            180.0,
            today.minusDays(3),
            today.plusDays(18),
            userNick);
    challengeNatationEndurance.getBadges().add(badgeNatation);

    Challenge challengeCyclisme80k =
        createSportChallenge(
            sportCyclisme,
            "Challenge Cyclisme 80K",
            "Atteindre 80 km en " + sportCyclisme.getName() + ".",
            ChallengeType.DISTANCE,
            80.0,
            today.minusDays(7),
            today.plusDays(28),
            userStoick);
    challengeCyclisme80k.getBadges().add(badgeCyclisme);

    Challenge challengeEscaladeVitesse =
        createSportChallenge(
            sportEscaladeVitesse,
            "Challenge Escalade Vitesse",
            "Cumuler 1500 calories sur des seances de " + sportEscaladeVitesse.getName() + ".",
            ChallengeType.CALORIE,
            1500.0,
            today.minusDays(2),
            today.plusDays(20),
            userAstrid);
    challengeEscaladeVitesse.getBadges().addAll(List.of(badgeEscalade, badgeMarathonien));

    Challenge challengeMusculationRegulier =
        createSportChallenge(
            sportMusculation,
            "Challenge Musculation Regulier",
            "Enregistrer 12 seances de " + sportMusculation.getName() + ".",
            ChallengeType.REPETITION,
            12.0,
            today.minusDays(1),
            today.plusDays(30),
            userBogo);
    challengeMusculationRegulier.getBadges().add(badgeYoga);

    challengeRepository.saveAll(
        List.of(
            challengeRunning25k,
            challengeNatationEndurance,
            challengeCyclisme80k,
            challengeEscaladeVitesse,
            challengeMusculationRegulier));

    // Goals (associes a des personnes precises)
    Goal goalAliceCourse =
        createGoal(
            "Objectif running mensuel Alice", GoalType.DISTANCE, 50.0, 22.5, "km", userAlice);
    Goal goalNickNatation =
        createGoal("Objectif natation Nick", GoalType.DUREE, 240.0, 75.0, "min", userNick);
    Goal goalAstridEscalade =
        createGoal(
            "Objectif escalade Astrid", GoalType.CALORIES, 1800.0, 650.0, "kcal", userAstrid);
    Goal goalStoickCyclisme =
        createGoal("Objectif cyclisme Stoick", GoalType.DISTANCE, 120.0, 48.0, "km", userStoick);
    Goal goalBogoMusculation =
        createGoal(
            "Objectif musculation Bogo", GoalType.REPETITIONS, 300.0, 120.0, "reps", userBogo);

    List<Goal> seededGoals =
        goalRepository.saveAll(
            List.of(
                goalAliceCourse,
                goalNickNatation,
                goalAstridEscalade,
                goalStoickCyclisme,
                goalBogoMusculation));

    // Attribution badges + goals a certaines personnes
    userAlice.getBadges().addAll(List.of(badgeRookie5k, badgeMarathonHerbe));
    userAlice.getGoals().add(seededGoals.get(0));

    userNick.getBadges().add(badgeNatation);
    userNick.getGoals().add(seededGoals.get(1));

    userAstrid.getBadges().addAll(List.of(badgeEscalade, badgeMarathonien));
    userAstrid.getGoals().add(seededGoals.get(2));

    userStoick.getBadges().add(badgeCyclisme);
    userStoick.getGoals().add(seededGoals.get(3));

    userBogo.getBadges().add(badgeYoga);
    userBogo.getGoals().add(seededGoals.get(4));

    userRepository.saveAll(List.of(userAlice, userNick, userAstrid, userStoick, userBogo));

    WeatherStatsDTO clearsky =
        new WeatherStatsDTO("22", "23", "21", "20", "0.00", "10", "clearsky");
    WeatherStatsDTO cloudy = new WeatherStatsDTO("18", "19", "17", "16", "0.10", "12", "cloudy");
    WeatherStatsDTO rain = new WeatherStatsDTO("15", "16", "14", "13", "1.00", "8", "rain");

    Workout workoutKudo =
        createWorkout(
            LocalDateTime.of(2026, 4, 1, 10, 0),
            4.2,
            32.0,
            "Toulouse",
            4,
            clearsky,
            sportCourseCanal,
            userJudy);
    workoutKudo.addKudo(userTaiLung);
    workoutKudo.addKudo(userShifu);
    workoutKudo.addKudo(userOogway);
    workoutKudo.addKudo(userAlice);
    workoutKudo.addKudo(userBenoit);
    workoutKudo.addKudo(userOwen);
    workoutKudo.addKudo(userHiccup);

    // Workouts
    workoutRepository.saveAll(
        List.of(
            workoutKudo,
            createWorkout(
                LocalDateTime.of(2026, 4, 3, 10, 0),
                3.8,
                29.0,
                "Toulouse",
                4,
                cloudy,
                sportParcoursAgilite,
                userJudy),
            createWorkout(
                LocalDateTime.of(2026, 4, 6, 10, 0),
                5.0,
                37.0,
                "Toulouse",
                5,
                clearsky,
                sportCoursePied,
                userJudy),
            createWorkout(
                LocalDateTime.of(2026, 4, 2, 10, 0),
                6.5,
                43.0,
                "Toulouse",
                4,
                cloudy,
                sportSprintCote,
                userNick),
            createWorkout(
                LocalDateTime.of(2026, 4, 5, 10, 0),
                2.4,
                30.0,
                "Toulouse",
                3,
                rain,
                sportNatation,
                userNick),
            createWorkout(
                LocalDateTime.of(2026, 4, 8, 10, 0),
                7.2,
                48.0,
                "Toulouse",
                5,
                cloudy,
                sportCoursePied,
                userNick),
            createWorkout(
                LocalDateTime.of(2026, 4, 1, 10, 0),
                3.0,
                36.0,
                "Toulouse",
                4,
                null,
                sportCircuitCardio,
                userBogo),
            createWorkout(
                LocalDateTime.of(2026, 4, 4, 10, 0),
                4.4,
                38.0,
                "Toulouse",
                4,
                null,
                sportRenforcementFonctionnel,
                userBogo),
            createWorkout(
                LocalDateTime.of(2026, 4, 9, 10, 0),
                2.2,
                28.0,
                "Toulouse",
                3,
                null,
                sportEscaladeVitesse,
                userBogo),
            createWorkout(
                LocalDateTime.of(2026, 4, 2, 10, 0),
                3.7,
                31.0,
                "Toulouse",
                3,
                null,
                sportMobiliteActive,
                userBellwether),
            createWorkout(
                LocalDateTime.of(2026, 4, 7, 10, 0),
                2.9,
                27.0,
                "Toulouse",
                4,
                null,
                sportNatation,
                userBellwether),
            createWorkout(
                LocalDateTime.of(2026, 4, 10, 10, 0),
                4.1,
                33.0,
                "Toulouse",
                4,
                null,
                sportCircuitCardio,
                userBellwether),
            createWorkout(
                LocalDateTime.of(2026, 4, 1, 10, 0),
                12.0,
                58.0,
                "Toulouse",
                5,
                clearsky,
                sportFractionneIntense,
                userHiccup),
            createWorkout(
                LocalDateTime.of(2026, 4, 4, 10, 0),
                6.0,
                45.0,
                "Toulouse",
                4,
                null,
                sportEnduranceMixte,
                userHiccup),
            createWorkout(
                LocalDateTime.of(2026, 4, 11, 10, 0),
                8.3,
                52.0,
                "Pau",
                5,
                null,
                sportCoursePied,
                userHiccup),
            createWorkout(
                LocalDateTime.of(2026, 4, 3, 10, 0),
                5.5,
                41.0,
                "Toulouse",
                4,
                null,
                sportFootball,
                userAstrid),
            createWorkout(
                LocalDateTime.of(2026, 4, 7, 10, 0),
                4.6,
                36.0,
                "Tarbes",
                4,
                null,
                sportEscaladeVitesse,
                userAstrid),
            createWorkout(
                LocalDateTime.of(2026, 4, 12, 10, 0),
                7.0,
                49.0,
                "Limoges",
                3,
                null,
                sportSautParachute,
                userAstrid),
            createWorkout(
                LocalDateTime.of(2026, 4, 2, 10, 0),
                4.8,
                44.0,
                "Marseille",
                4,
                null,
                sportMusculation,
                userStoick),
            createWorkout(
                LocalDateTime.of(2026, 4, 6, 10, 0),
                3.1,
                35.0,
                "Toulouse",
                3,
                null,
                sportPlongee,
                userStoick),
            createWorkout(
                LocalDateTime.of(2026, 4, 13, 10, 0),
                6.2,
                50.0,
                "Strasbourg",
                5,
                null,
                sportCyclisme,
                userStoick),
            createWorkout(
                LocalDateTime.of(2026, 4, 2, 10, 0),
                4.0,
                39.0,
                "Perpignan",
                4,
                null,
                sportBasketball,
                userFishlegs),
            createWorkout(
                LocalDateTime.of(2026, 4, 8, 10, 0),
                3.3,
                33.0,
                "Marseille",
                4,
                null,
                sportEscaladeBloc,
                userFishlegs),
            createWorkout(
                LocalDateTime.of(2026, 4, 14, 10, 0),
                5.9,
                47.0,
                "Paris",
                4,
                null,
                sportCoursePied,
                userFishlegs),
            createWorkout(
                LocalDateTime.of(2026, 4, 3, 10, 0),
                6.1,
                42.0,
                "Toulouse",
                4,
                null,
                sportParkourUrbain,
                userRodney),
            createWorkout(
                LocalDateTime.of(2026, 4, 9, 10, 0),
                4.9,
                37.0,
                "Toulouse",
                4,
                null,
                sportCircuitCardio,
                userRodney),
            createWorkout(
                LocalDateTime.of(2026, 4, 15, 10, 0),
                7.4,
                51.0,
                "Toulouse",
                5,
                clearsky,
                sportCyclisme,
                userRodney),
            createWorkout(
                LocalDateTime.of(2026, 4, 4, 10, 0),
                3.6,
                30.0,
                "Toulouse",
                3,
                null,
                sportMobiliteActive,
                userCappy),
            createWorkout(
                LocalDateTime.of(2026, 4, 10, 10, 0),
                4.2,
                34.0,
                "Toulouse",
                4,
                null,
                sportNatation,
                userCappy),
            createWorkout(
                LocalDateTime.of(2026, 4, 16, 10, 0),
                5.0,
                39.0,
                "Toulouse",
                3,
                null,
                sportYogaDynamique,
                userCappy),
            createWorkout(
                LocalDateTime.of(2026, 4, 5, 10, 0),
                5.2,
                40.0,
                "Toulouse",
                4,
                null,
                sportCircuitCardio,
                userFender),
            createWorkout(
                LocalDateTime.of(2026, 4, 11, 10, 0),
                4.5,
                35.0,
                "Toulouse",
                4,
                null,
                sportParkourUrbain,
                userFender),
            createWorkout(
                LocalDateTime.of(2026, 4, 17, 10, 0),
                6.6,
                46.0,
                "Toulouse",
                4,
                null,
                sportPlongee,
                userFender),
            createWorkout(
                LocalDateTime.of(2026, 4, 6, 10, 0),
                4.7,
                43.0,
                "Toulouse",
                4,
                null,
                sportRenforcementFonctionnel,
                userBigweld),
            createWorkout(
                LocalDateTime.of(2026, 4, 12, 10, 0),
                5.3,
                44.0,
                "Toulouse",
                4,
                null,
                sportEscaladeBloc,
                userBigweld),
            createWorkout(
                LocalDateTime.of(2026, 4, 18, 10, 0),
                7.1,
                53.0,
                "Toulouse",
                5,
                null,
                sportRandonnee,
                userBigweld)));
  }

  private Workout createWorkout(
      LocalDateTime date,
      Double distance,
      Double duration,
      String address,
      Integer rating,
      WeatherStatsDTO weather,
      Sport sport,
      User user) {
    return new Workout(date, distance, duration, address, rating, weather, sport, user);
  }

  private Goal createGoal(
      String label,
      GoalType type,
      Double targetValue,
      Double currentValue,
      String unit,
      User user) {
    return new Goal(label, type, targetValue, currentValue, unit, user);
  }

  private Sport createSport(String name, Double calPerMin) {
    return new Sport(name, calPerMin);
  }

  private Badge createSportBadge(Sport sport, String suffix, String description, String iconPath) {
    return new Badge(sport.getName() + " - " + suffix, description, iconPath);
  }

  private Challenge createSportChallenge(
      Sport sport,
      String title,
      String description,
      ChallengeType type,
      Double targetValue,
      LocalDate startDate,
      LocalDate endDate,
      User creator) {
    return new Challenge(
        title + " (" + sport.getName() + ")",
        description,
        type,
        targetValue,
        startDate,
        endDate,
        creator);
  }

  private void assignDemoAvatars(Map<User, String> avatarByUser) {
    try {
      Path uploadDir = Paths.get(avatarUploadDir).toAbsolutePath().normalize();
      Files.createDirectories(uploadDir);
      for (Map.Entry<User, String> entry : avatarByUser.entrySet()) {
        User user = entry.getKey();
        String sourceFileName = entry.getValue();
        if (user == null
            || user.getId() == null
            || sourceFileName == null
            || sourceFileName.isBlank()) {
          continue;
        }

        cleanupExistingUserAvatars(uploadDir, user.getId());

        String extension = extractExtension(sourceFileName);
        if (extension.isBlank()) {
          continue;
        }

        String targetFileName = "user_" + user.getId() + "." + extension;
        Path targetPath = uploadDir.resolve(targetFileName).normalize();
        if (!targetPath.startsWith(uploadDir)) {
          continue;
        }

        ClassPathResource resource =
            new ClassPathResource("static/images/avatars/" + sourceFileName);
        if (!resource.exists()) {
          continue;
        }

        try (InputStream inputStream = resource.getInputStream()) {
          Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
          user.setProfileImagePath("/avatar_upload/" + targetFileName);
          userRepository.save(user);
        }
      }
    } catch (IOException exception) {
      throw new IllegalStateException("Impossible de copier les avatars de demo.", exception);
    }
  }

  private String extractExtension(String filename) {
    if (filename == null || !filename.contains(".")) {
      return "";
    }
    int lastDotIndex = filename.lastIndexOf('.');
    if (lastDotIndex == filename.length() - 1) {
      return "";
    }
    return filename.substring(lastDotIndex + 1);
  }

  private void cleanupExistingUserAvatars(Path uploadDir, Long userId) throws IOException {
    String pattern = "user_" + userId + ".*";
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(uploadDir, pattern)) {
      for (Path path : stream) {
        Files.deleteIfExists(path);
      }
    }
  }

  @SuppressWarnings("java:S6437") // Demo seed credential; not used outside local sample data.
  private User createUser(
      String firstname,
      String lastname,
      String email,
      Double weight,
      Double height,
      Sex sex,
      LocalDate birthDate,
      PracticeLevel level) {
    User user = new User(firstname, lastname, email, weight, height, sex, birthDate, level);
    user.setProfileImagePath(null);
    user.setPassword(passwordEncoder.encode("demo123"));
    user.setRole(Role.USER);
    return user;
  }
}
