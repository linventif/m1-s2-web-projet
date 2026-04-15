package web.sportflow.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import web.sportflow.badge.Badge;
import web.sportflow.badge.BadgeRepository;
import web.sportflow.challenge.Challenge;
import web.sportflow.challenge.ChallengeRepository;
import web.sportflow.challenge.ChallengeType;
import web.sportflow.exercise.Exercise;
import web.sportflow.exercise.ExerciseRepository;
import web.sportflow.friendship.FriendshipService;
import web.sportflow.goal.Goal;
import web.sportflow.goal.GoalRepository;
import web.sportflow.goal.GoalType;
import web.sportflow.sport.Sport;
import web.sportflow.sport.SportName;
import web.sportflow.sport.SportRepository;
import web.sportflow.user.PracticeLevel;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;
import web.sportflow.user.UserRepository;
import web.sportflow.weather.WeatherStatsDTO;
import web.sportflow.workout.Workout;
import web.sportflow.workout.WorkoutExercise;
import web.sportflow.workout.WorkoutRepository;

@Component
@ConditionalOnProperty(
    name = "app.data-initializer.enabled",
    havingValue = "true",
    matchIfMissing = false)
public class ReferenceDataInitializer implements CommandLineRunner {

  private final WorkoutRepository workoutRepository;
  private final UserRepository userRepository;
  private final SportRepository sportRepository;
  private final BadgeRepository badgeRepository;
  private final ChallengeRepository challengeRepository;
  private final GoalRepository goalRepository;
  private final PasswordEncoder passwordEncoder;
  private final FriendshipService friendshipService;
  private final ExerciseRepository exerciseRepository;

  @Value("${app.avatar-upload-dir:upload_data/images/avatar}")
  private String avatarUploadDir;

  @Value("${app.badge-upload-dir:upload_data/images/badge}")
  private String badgeUploadDir;

  public ReferenceDataInitializer(
      UserRepository userRepository,
      SportRepository sportRepository,
      BadgeRepository badgeRepository,
      ChallengeRepository challengeRepository,
      GoalRepository goalRepository,
      PasswordEncoder passwordEncoder,
      WorkoutRepository workoutRepository,
      FriendshipService friendshipService,
      ExerciseRepository exerciseRepository) {
    this.userRepository = userRepository;
    this.sportRepository = sportRepository;
    this.badgeRepository = badgeRepository;
    this.challengeRepository = challengeRepository;
    this.goalRepository = goalRepository;
    this.passwordEncoder = passwordEncoder;
    this.workoutRepository = workoutRepository;
    this.friendshipService = friendshipService;
    this.exerciseRepository = exerciseRepository;
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
    Sport sportCoursePied = createSport(SportName.Course, 9.8);
    Sport sportNatation = createSport(SportName.Natation, 8.3);
    Sport sportCyclisme = createSport(SportName.Cyclisme, 8.0);
    Sport sportFootball = createSport(SportName.Football, 7.0);
    Sport sportBasketball = createSport(SportName.Basketball, 8.0);
    Sport sportTennis = createSport(SportName.Tennis, 7.3);
    Sport sportMusculation = createSport(SportName.Musculation, 6.0);
    Sport sportEscaladeBloc = createSport(SportName.Escalade, 8.0);
    Sport sportYogaDynamique = createSport(SportName.Yoga, 3.3);
    Sport sportRandonnee = createSport(SportName.Randonnee, 6.0);
    Sport sportSautParachute = createSport(SportName.Parachute, 3.5);
    Sport sportPlongee = createSport(SportName.Plongee, 7.0);
    Sport sportParkourUrbain = createSport(SportName.Parkour, 8.5);
    Sport sportCircuitCardio = createSport(SportName.Cardio, 8.0);
    Sport sportMobiliteActive = createSport(SportName.Mobilite, 3.3);
    Sport sportCourseCanal = sportCoursePied;
    Sport sportSprintCote = sportCoursePied;
    Sport sportParcoursAgilite = sportCoursePied;
    Sport sportFractionneIntense = sportCoursePied;
    Sport sportRenforcementFonctionnel = sportMusculation;
    Sport sportEnduranceMixte = sportCoursePied;
    Sport sportEscaladeVitesse = sportEscaladeBloc;

    sportRepository.saveAll(
        List.of(
            sportCourseCanal,
            sportParkourUrbain,
            sportCircuitCardio,
            sportMobiliteActive,
            sportEscaladeBloc,
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

    Exercise exerciseCourseContinue = createExercice("Course continue", null);
    Exercise exerciseSprint = createExercice("Sprint", null);
    Exercise exerciseMonteeCote = createExercice("Montee de cote", null);
    Exercise exerciseCircuitCardio = createExercice("Circuit cardio", null);
    Exercise exerciseBurpees = createExercice("Burpees", null);
    Exercise exercisePompes = createExercice("Pompes", null);
    Exercise exerciseSquat = createExercice("Squat", null);
    Exercise exerciseDeveloppeCouche = createExercice("Developpe couche", 0.12);
    Exercise exerciseDeveloppeMilitaire = createExercice("Developpe militaire", 0.10);
    Exercise exerciseTractions = createExercice("Tractions", null);
    Exercise exerciseNageLibre = createExercice("Nage libre", null);
    Exercise exerciseCrawl = createExercice("Crawl", null);
    Exercise exerciseEscaladeBloc = createExercice("Escalade bloc", null);
    Exercise exerciseVoieVitesse = createExercice("Voie de vitesse", null);
    Exercise exercisePedalage = createExercice("Pedalage endurance", null);
    Exercise exerciseDribble = createExercice("Dribble et tirs", null);
    Exercise exerciseYogaFlow = createExercice("Yoga flow", null);
    Exercise exerciseMarcheSentier = createExercice("Marche sur sentier", null);

    linkExercises(sportCourseCanal, exerciseCourseContinue, exerciseSprint, exerciseMonteeCote);
    linkExercises(sportSprintCote, exerciseSprint, exerciseMonteeCote);
    linkExercises(sportParcoursAgilite, exerciseSprint, exerciseBurpees, exerciseTractions);
    linkExercises(sportFractionneIntense, exerciseSprint, exerciseCourseContinue, exerciseBurpees);
    linkExercises(
        sportRenforcementFonctionnel,
        exercisePompes,
        exerciseSquat,
        exerciseTractions,
        exerciseBurpees);
    linkExercises(sportEnduranceMixte, exerciseCourseContinue, exerciseCircuitCardio);
    linkExercises(sportParkourUrbain, exerciseSprint, exerciseTractions, exerciseSquat);
    linkExercises(sportCircuitCardio, exerciseCircuitCardio, exerciseBurpees, exercisePompes);
    linkExercises(sportMobiliteActive, exerciseYogaFlow, exerciseSquat);
    linkExercises(sportEscaladeVitesse, exerciseVoieVitesse, exerciseTractions);
    linkExercises(sportEscaladeBloc, exerciseEscaladeBloc, exerciseTractions);
    linkExercises(sportCoursePied, exerciseCourseContinue, exerciseSprint);
    linkExercises(sportNatation, exerciseNageLibre, exerciseCrawl);
    linkExercises(sportSautParachute, exerciseSquat);
    linkExercises(sportPlongee, exerciseNageLibre);
    linkExercises(sportFootball, exerciseSprint, exerciseCourseContinue, exerciseDribble);
    linkExercises(sportCyclisme, exercisePedalage);
    linkExercises(sportBasketball, exerciseSprint, exerciseDribble);
    linkExercises(sportTennis, exerciseSprint);
    linkExercises(
        sportMusculation,
        exerciseDeveloppeCouche,
        exerciseDeveloppeMilitaire,
        exerciseSquat,
        exerciseTractions);
    linkExercises(sportYogaDynamique, exerciseYogaFlow);
    linkExercises(sportRandonnee, exerciseMarcheSentier);

    exerciseRepository.saveAll(
        List.of(
            exerciseCourseContinue,
            exerciseSprint,
            exerciseMonteeCote,
            exerciseCircuitCardio,
            exerciseBurpees,
            exercisePompes,
            exerciseSquat,
            exerciseDeveloppeCouche,
            exerciseDeveloppeMilitaire,
            exerciseTractions,
            exerciseNageLibre,
            exerciseCrawl,
            exerciseEscaladeBloc,
            exerciseVoieVitesse,
            exercisePedalage,
            exerciseDribble,
            exerciseYogaFlow,
            exerciseMarcheSentier));

    sportRepository.saveAll(
        List.of(
            sportCourseCanal,
            sportParkourUrbain,
            sportCircuitCardio,
            sportMobiliteActive,
            sportEscaladeBloc,
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
    assignDemoBadgeIcons(demoBadges);
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
            null,
            LocalDateTime.of(2026, 4, 1, 10, 0),
            "Toulouse",
            clearsky,
            new ArrayList<>(),
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
                "Course du canal",
                LocalDateTime.of(2026, 4, 1, 10, 0),
                "Toulouse",
                clearsky,
                List.of(
                    createWorkoutExercise(
                        150.0, 6500.0, 2400.0, null, null, null, null, exerciseCourseContinue),
                    createWorkoutExercise(
                        170.0, null, 300.0, null, null, null, null, exerciseSprint)),
                sportCourseCanal,
                userJudy),
            createWorkout(
                "Parcours agilité",
                LocalDateTime.of(2026, 4, 3, 10, 0),
                "Toulouse",
                cloudy,
                List.of(
                    createWorkoutExercise(
                        155.0, 800.0, 900.0, null, null, null, null, exerciseSprint),
                    createWorkoutExercise(160.0, null, 600.0, 12, 4, null, null, exerciseBurpees)),
                sportParcoursAgilite,
                userJudy),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 6, 10, 0),
                "Toulouse",
                clearsky,
                new ArrayList<>(),
                sportCoursePied,
                userJudy),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 2, 10, 0),
                "Toulouse",
                cloudy,
                List.of(
                    createWorkoutExercise(
                        178.0, 1200.0, 420.0, null, null, null, null, exerciseSprint),
                    createWorkoutExercise(
                        172.0, 900.0, 480.0, null, null, null, null, exerciseMonteeCote)),
                sportSprintCote,
                userNick),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 5, 10, 0),
                "Toulouse",
                rain,
                List.of(
                    createWorkoutExercise(
                        135.0, 1000.0, 1800.0, null, null, null, null, exerciseCrawl),
                    createWorkoutExercise(
                        125.0, 600.0, 900.0, null, null, null, null, exerciseNageLibre)),
                sportNatation,
                userNick),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 8, 10, 0),
                "Toulouse",
                cloudy,
                new ArrayList<>(),
                sportCoursePied,
                userNick),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 1, 10, 0),
                "Toulouse",
                null,
                List.of(
                    createWorkoutExercise(155.0, null, 900.0, 15, 4, null, null, exerciseBurpees),
                    createWorkoutExercise(145.0, null, 600.0, 20, 4, null, null, exercisePompes)),
                sportCircuitCardio,
                userBogo),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 4, 10, 0),
                "Toulouse",
                null,
                List.of(
                    createWorkoutExercise(130.0, null, 720.0, 12, 4, null, null, exerciseSquat),
                    createWorkoutExercise(140.0, null, 600.0, 8, 4, null, null, exerciseTractions)),
                sportRenforcementFonctionnel,
                userBogo),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 9, 10, 0),
                "Toulouse",
                null,
                List.of(
                    createWorkoutExercise(
                        160.0, 120.0, 900.0, null, null, null, null, exerciseVoieVitesse),
                    createWorkoutExercise(145.0, null, 600.0, 8, 3, null, null, exerciseTractions)),
                sportEscaladeVitesse,
                userBogo),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 2, 10, 0),
                "Toulouse",
                null,
                new ArrayList<>(),
                sportMobiliteActive,
                userBellwether),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 7, 10, 0),
                "Toulouse",
                null,
                new ArrayList<>(),
                sportNatation,
                userBellwether),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 10, 10, 0),
                "Toulouse",
                null,
                new ArrayList<>(),
                sportCircuitCardio,
                userBellwether),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 1, 10, 0),
                "Toulouse",
                clearsky,
                new ArrayList<>(),
                sportFractionneIntense,
                userHiccup),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 4, 10, 0),
                "Toulouse",
                null,
                new ArrayList<>(),
                sportEnduranceMixte,
                userHiccup),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 11, 10, 0),
                "Pau",
                null,
                new ArrayList<>(),
                sportCoursePied,
                userHiccup),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 3, 10, 0),
                "Toulouse",
                null,
                new ArrayList<>(),
                sportFootball,
                userAstrid),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 7, 10, 0),
                "Tarbes",
                null,
                new ArrayList<>(),
                sportEscaladeVitesse,
                userAstrid),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 12, 10, 0),
                "Limoges",
                null,
                new ArrayList<>(),
                sportSautParachute,
                userAstrid),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 2, 10, 0),
                "Marseille",
                null,
                List.of(
                    createWorkoutExercise(
                        110.0, null, 900.0, 10, 4, 80000.0, null, exerciseDeveloppeCouche),
                    createWorkoutExercise(
                        115.0, null, 720.0, 8, 4, 45000.0, null, exerciseDeveloppeMilitaire),
                    createWorkoutExercise(125.0, null, 900.0, 12, 4, 90000.0, null, exerciseSquat)),
                sportMusculation,
                userStoick),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 6, 10, 0),
                "Toulouse",
                null,
                new ArrayList<>(),
                sportPlongee,
                userStoick),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 13, 10, 0),
                "Strasbourg",
                null,
                List.of(
                    createWorkoutExercise(
                        145.0, 22000.0, 3600.0, null, null, null, null, exercisePedalage)),
                sportCyclisme,
                userStoick),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 2, 10, 0),
                "Perpignan",
                null,
                List.of(
                    createWorkoutExercise(
                        150.0, 2500.0, 1800.0, null, null, null, null, exerciseDribble),
                    createWorkoutExercise(
                        165.0, 700.0, 600.0, null, null, null, null, exerciseSprint)),
                sportBasketball,
                userFishlegs),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 8, 10, 0),
                "Marseille",
                null,
                List.of(
                    createWorkoutExercise(
                        145.0, 80.0, 1500.0, null, null, null, null, exerciseEscaladeBloc),
                    createWorkoutExercise(135.0, null, 600.0, 6, 4, null, null, exerciseTractions)),
                sportEscaladeBloc,
                userFishlegs),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 14, 10, 0),
                "Paris",
                null,
                new ArrayList<>(),
                sportCoursePied,
                userFishlegs),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 3, 10, 0),
                "Toulouse",
                null,
                new ArrayList<>(),
                sportParkourUrbain,
                userRodney),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 9, 10, 0),
                "Toulouse",
                null,
                new ArrayList<>(),
                sportCircuitCardio,
                userRodney),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 15, 10, 0),
                "Toulouse",
                null,
                new ArrayList<>(),
                sportCyclisme,
                userRodney),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 4, 10, 0),
                "Toulouse",
                null,
                new ArrayList<>(),
                sportMobiliteActive,
                userCappy),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 10, 10, 0),
                "Toulouse",
                null,
                new ArrayList<>(),
                sportNatation,
                userCappy),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 16, 10, 0),
                "Toulouse",
                null,
                List.of(
                    createWorkoutExercise(
                        95.0, null, 2400.0, null, null, null, null, exerciseYogaFlow)),
                sportYogaDynamique,
                userCappy),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 5, 10, 0),
                "Toulouse",
                null,
                new ArrayList<>(),
                sportCircuitCardio,
                userFender),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 11, 10, 0),
                "Toulouse",
                null,
                new ArrayList<>(),
                sportParkourUrbain,
                userFender),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 17, 10, 0),
                "Toulouse",
                null,
                new ArrayList<>(),
                sportPlongee,
                userFender),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 6, 10, 0),
                "Toulouse",
                null,
                new ArrayList<>(),
                sportRenforcementFonctionnel,
                userBigweld),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 12, 10, 0),
                "Toulouse",
                null,
                new ArrayList<>(),
                sportEscaladeBloc,
                userBigweld),
            createWorkout(
                null,
                LocalDateTime.of(2026, 4, 18, 10, 0),
                "Toulouse",
                null,
                List.of(
                    createWorkoutExercise(
                        125.0, 12000.0, 5400.0, null, null, null, null, exerciseMarcheSentier)),
                sportRandonnee,
                userBigweld)));
  }

  private Workout createWorkout(
      String name,
      LocalDateTime date,
      String address,
      WeatherStatsDTO weather,
      List<WorkoutExercise> exercises,
      Sport sport,
      User user) {
    if (exercises.isEmpty()) return new Workout(name, date, address, weather, sport, user);

    Workout workout = new Workout(name, date, address, weather, exercises, sport, user);
    for (WorkoutExercise exercise : exercises) {
      exercise.setWorkout(workout);
    }
    return workout;
  }

  private Sport createSport(SportName name, Double met) {
    return new Sport(name, met);
  }

  private WorkoutExercise createWorkoutExercise(
      Double averageBps,
      Double distanceM,
      Double durationSec,
      Integer reps,
      Integer sets,
      Double weightG,
      Workout workout,
      Exercise exercise) {
    if (distanceM == null)
      return new WorkoutExercise(averageBps, durationSec, reps, sets, weightG, workout, exercise);
    if (weightG == null && reps == null && sets == null)
      return new WorkoutExercise(averageBps, distanceM, durationSec, workout, exercise);
    return new WorkoutExercise(averageBps, distanceM, durationSec, reps, sets, workout, exercise);
  }

  private Exercise createExercice(String name, Double calPerSec) {
    return new Exercise(name, calPerSec, new ArrayList<>(), new ArrayList<>());
  }

  private void linkExercises(Sport sport, Exercise... exercises) {
    for (Exercise exercise : exercises) {
      if (!sport.getExercises().contains(exercise)) {
        sport.getExercises().add(exercise);
      }
    }
    for (Exercise exercise : exercises) {
      if (!exercise.getSports().contains(sport)) {
        exercise.getSports().add(sport);
      }
    }
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

  private void assignDemoBadgeIcons(List<Badge> badges) {
    try {
      Path uploadDir = Paths.get(badgeUploadDir).toAbsolutePath().normalize();
      Files.createDirectories(uploadDir);
      for (Badge badge : badges) {
        if (badge == null) {
          continue;
        }
        String sourceFileName = extractFileName(badge.getIconPath());
        if (sourceFileName.isBlank()) {
          continue;
        }
        String extension = extractExtension(sourceFileName);
        if (extension.isBlank()) {
          continue;
        }

        Path targetPath = uploadDir.resolve(sourceFileName).normalize();
        if (!targetPath.startsWith(uploadDir)) {
          continue;
        }

        ClassPathResource resource = new ClassPathResource("static/images/badge/" + sourceFileName);
        if (!resource.exists()) {
          continue;
        }

        try (InputStream inputStream = resource.getInputStream()) {
          Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
          badge.setIconPath("/badge_upload/" + sourceFileName);
        }
      }
      badgeRepository.saveAll(badges);
    } catch (IOException exception) {
      throw new IllegalStateException("Impossible de copier les badges de demo.", exception);
    }
  }

  private String extractFileName(String pathValue) {
    if (pathValue == null || pathValue.isBlank()) {
      return "";
    }
    String normalizedPath = pathValue.trim().replace('\\', '/');
    int queryIndex = normalizedPath.indexOf('?');
    if (queryIndex >= 0) {
      normalizedPath = normalizedPath.substring(0, queryIndex);
    }
    int fragmentIndex = normalizedPath.indexOf('#');
    if (fragmentIndex >= 0) {
      normalizedPath = normalizedPath.substring(0, fragmentIndex);
    }
    int lastSlashIndex = normalizedPath.lastIndexOf('/');
    String fileName =
        lastSlashIndex >= 0 ? normalizedPath.substring(lastSlashIndex + 1) : normalizedPath;
    if (fileName.isBlank() || fileName.contains("..")) {
      return "";
    }
    return fileName;
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
