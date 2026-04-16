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
import web.sportflow.workout.comment.Comment;

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
            Map.entry(userAlice, "alice_martin.png"),
            Map.entry(userBenoit, "benoit_leroy.png"),
            Map.entry(userOwen, "owen_mercier.png"),
            Map.entry(userAdmin, "admin_bg.png"),
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
    Sport sportEscalade = createSport(SportName.Escalade, 8.0);
    Sport sportAlpinisme = createSport(SportName.Alpinisme, 7.5);
    Sport sportRandonnee = createSport(SportName.Randonnee, 6.0);
    Sport sportCourse = createSport(SportName.Course, 9.8);
    Sport sportLance = createSport(SportName.Lance, 4.5);
    Sport sportMarathon = createSport(SportName.Marathon, 10.5);
    Sport sportMarche = createSport(SportName.Marche, 4.3);
    Sport sportSaut = createSport(SportName.Saut, 6.5);
    Sport sportCyclisme = createSport(SportName.Cyclisme, 8.0);
    Sport sportMusculation = createSport(SportName.Musculation, 6.0);
    Sport sportCallisthenie = createSport(SportName.Callisthenie, 5.5);
    Sport sportCrossFit = createSport(SportName.CrossFit, 8.0);
    Sport sportNatation = createSport(SportName.Natation, 8.3);
    Sport sportPlongee = createSport(SportName.Plongee, 7.0);
    Sport sportSautParachute = createSport(SportName.Saut_Parachute, 3.5);
    Sport sportBaseJump = createSport(SportName.Base_Jump, 4.0);
    Sport sportTennis = createSport(SportName.Tennis, 7.3);
    Sport sportPingPong = createSport(SportName.Ping_Pong, 4.0);
    Sport sportSquash = createSport(SportName.Squash, 7.3);
    Sport sportFootball = createSport(SportName.Football, 7.0);
    Sport sportBasketball = createSport(SportName.Basketball, 8.0);
    Sport sportJudo = createSport(SportName.Judo, 10.3);
    Sport sportTaekwondo = createSport(SportName.Taekwondo, 10.3);
    Sport sportKarate = createSport(SportName.Karate, 10.0);
    Sport sportBoxe = createSport(SportName.Boxe, 12.8);
    Sport sportEscrime = createSport(SportName.Escrime, 6.0);
    Sport sportLutte = createSport(SportName.Lutte, 10.3);
    Sport sportSki = createSport(SportName.Ski, 7.0);
    Sport sportCurling = createSport(SportName.Curling, 4.0);
    Sport sportHockey = createSport(SportName.Hockey, 8.0);
    Sport sportLuge = createSport(SportName.Luge, 4.0);
    Sport sportPatinage = createSport(SportName.Patinage, 7.0);
    Sport sportBobsleigh = createSport(SportName.Bobsleigh, 5.0);
    Sport sportParkour = createSport(SportName.Parkour, 8.5);
    Sport sportSkate = createSport(SportName.Skate, 5.0);
    Sport sportTirSportif = createSport(SportName.Tir_Sportif, 2.5);
    Sport sportTirArc = createSport(SportName.Tir_Arc, 3.5);
    Sport sportTirCible = createSport(SportName.Tir_Cible, 2.5);
    Sport sportRepassageExtrem = createSport(SportName.Repassage_Extrem, 4.0);
    Sport sportGymnastique = createSport(SportName.Gymnastique, 5.5);
    Sport sportYoga = createSport(SportName.Yoga, 3.3);
    Sport sportPentathlon = createSport(SportName.Pentathlon, 8.0);
    Sport sportTriathlon = createSport(SportName.Triathlon, 9.5);
    Sport sportFormule1 = createSport(SportName.Formule_1, 4.0);
    Sport sportMotocyclisme = createSport(SportName.Motocyclisme, 4.0);
    Sport sportAviron = createSport(SportName.Aviron, 7.0);
    Sport sportCanoeKayak = createSport(SportName.Canoe_Kayak, 6.0);
    Sport sportSurf = createSport(SportName.Surf, 3.0);
    Sport sportVoile = createSport(SportName.Voile, 3.0);
    Sport sportEquitation = createSport(SportName.Equitation, 5.5);
    Sport sportSpeleologie = createSport(SportName.Speleologie, 6.0);

    List<Sport> allSports =
        List.of(
            sportEscalade,
            sportAlpinisme,
            sportRandonnee,
            sportCourse,
            sportLance,
            sportMarathon,
            sportMarche,
            sportSaut,
            sportCyclisme,
            sportMusculation,
            sportCallisthenie,
            sportCrossFit,
            sportNatation,
            sportPlongee,
            sportSautParachute,
            sportBaseJump,
            sportTennis,
            sportPingPong,
            sportSquash,
            sportFootball,
            sportBasketball,
            sportJudo,
            sportTaekwondo,
            sportKarate,
            sportBoxe,
            sportEscrime,
            sportLutte,
            sportSki,
            sportCurling,
            sportHockey,
            sportLuge,
            sportPatinage,
            sportBobsleigh,
            sportParkour,
            sportSkate,
            sportTirSportif,
            sportTirArc,
            sportTirCible,
            sportRepassageExtrem,
            sportGymnastique,
            sportYoga,
            sportPentathlon,
            sportTriathlon,
            sportFormule1,
            sportMotocyclisme,
            sportAviron,
            sportCanoeKayak,
            sportSurf,
            sportVoile,
            sportEquitation,
            sportSpeleologie);

    sportRepository.saveAll(allSports);

    Exercise exerciseCourseContinue = createExercice("Course continue", 0.15, new ArrayList<>());
    Exercise exerciseSprint = createExercice("Sprint", 0.22, new ArrayList<>());
    Exercise exerciseMonteeCote = createExercice("Montee de cote", 0.18, new ArrayList<>());
    Exercise exerciseCircuitCardio = createExercice("Circuit cardio", 0.16, new ArrayList<>());
    Exercise exerciseBurpees = createExercice("Burpees", 0.18, new ArrayList<>());
    Exercise exercisePompes = createExercice("Pompes", 0.10, new ArrayList<>());
    Exercise exerciseSquat = createExercice("Squat", 0.11, new ArrayList<>());
    Exercise exerciseDeveloppeCouche = createExercice("Developpe couche", 0.12, new ArrayList<>());
    Exercise exerciseDeveloppeMilitaire =
        createExercice("Developpe militaire", 0.10, new ArrayList<>());
    Exercise exerciseTractions = createExercice("Tractions", 0.13, new ArrayList<>());
    Exercise exerciseNageLibre = createExercice("Nage libre", 0.13, new ArrayList<>());
    Exercise exerciseCrawl = createExercice("Crawl", 0.14, new ArrayList<>());
    Exercise exerciseEscaladeBloc = createExercice("Escalade bloc", 0.14, new ArrayList<>());
    Exercise exerciseVoieVitesse = createExercice("Voie de vitesse", 0.16, new ArrayList<>());
    Exercise exercisePedalage = createExercice("Pedalage endurance", 0.14, new ArrayList<>());
    Exercise exerciseDribble = createExercice("Dribble et tirs", 0.12, new ArrayList<>());
    Exercise exerciseYogaFlow = createExercice("Yoga flow", 0.06, new ArrayList<>());
    Exercise exerciseMarcheSentier = createExercice("Marche sur sentier", 0.09, new ArrayList<>());
    Exercise exerciseSautTechnique = createExercice("Saut technique", 0.13, new ArrayList<>());
    Exercise exerciseLancerMedecineBall =
        createExercice("Lancer medecine ball", 0.09, new ArrayList<>());
    Exercise exerciseShadowBoxing = createExercice("Shadow boxing", 0.15, new ArrayList<>());
    Exercise exerciseTravailAppuis = createExercice("Travail des appuis", 0.11, new ArrayList<>());
    Exercise exerciseKata = createExercice("Kata technique", 0.10, new ArrayList<>());
    Exercise exerciseRandori = createExercice("Randori", 0.17, new ArrayList<>());
    Exercise exerciseGlisse = createExercice("Glisse endurance", 0.12, new ArrayList<>());
    Exercise exerciseTirPrecision = createExercice("Tir de precision", 0.04, new ArrayList<>());
    Exercise exerciseGainage = createExercice("Gainage", 0.07, new ArrayList<>());
    Exercise exerciseRame = createExercice("Rame endurance", 0.13, new ArrayList<>());
    Exercise exercisePagaie = createExercice("Pagaie endurance", 0.11, new ArrayList<>());
    Exercise exerciseEquilibre = createExercice("Equilibre", 0.05, new ArrayList<>());
    Exercise exercisePilotage = createExercice("Pilotage technique", 0.07, new ArrayList<>());

    linkExercises(sportCourse, exerciseCourseContinue, exerciseSprint, exerciseMonteeCote);
    linkExercises(sportCourse, exerciseSprint, exerciseMonteeCote);
    linkExercises(sportParkour, exerciseSprint, exerciseBurpees, exerciseTractions);
    linkExercises(sportCourse, exerciseSprint, exerciseCourseContinue, exerciseBurpees);
    linkExercises(
        sportCallisthenie, exercisePompes, exerciseSquat, exerciseTractions, exerciseBurpees);
    linkExercises(sportCourse, exerciseCourseContinue, exerciseCircuitCardio);
    linkExercises(sportParkour, exerciseSprint, exerciseTractions, exerciseSquat);
    linkExercises(sportEscalade, exerciseVoieVitesse, exerciseTractions, exerciseEscaladeBloc);
    linkExercises(sportCourse, exerciseCourseContinue, exerciseSprint);
    linkExercises(sportNatation, exerciseNageLibre, exerciseCrawl);
    linkExercises(sportSautParachute, exerciseSquat, exerciseGainage);
    linkExercises(sportPlongee, exerciseNageLibre, exerciseCrawl, exerciseGainage);
    linkExercises(sportFootball, exerciseSprint, exerciseCourseContinue, exerciseDribble);
    linkExercises(sportCyclisme, exercisePedalage, exerciseGainage);
    linkExercises(sportBasketball, exerciseSprint, exerciseDribble);
    linkExercises(sportTennis, exerciseSprint, exerciseTravailAppuis);
    linkExercises(
        sportMusculation,
        exerciseDeveloppeCouche,
        exerciseDeveloppeMilitaire,
        exerciseSquat,
        exerciseTractions);
    linkExercises(sportYoga, exerciseYogaFlow, exerciseEquilibre, exerciseGainage);
    linkExercises(sportRandonnee, exerciseMarcheSentier, exerciseGainage);
    linkExercises(sportAlpinisme, exerciseEscaladeBloc, exerciseTractions, exerciseMarcheSentier);
    linkExercises(sportLance, exerciseLancerMedecineBall, exerciseGainage);
    linkExercises(sportMarathon, exerciseCourseContinue, exerciseMonteeCote);
    linkExercises(sportMarche, exerciseMarcheSentier, exerciseEquilibre);
    linkExercises(sportSaut, exerciseSautTechnique, exerciseSquat);
    linkExercises(
        sportCallisthenie, exercisePompes, exerciseTractions, exerciseSquat, exerciseGainage);
    linkExercises(
        sportCrossFit, exerciseCircuitCardio, exerciseBurpees, exercisePompes, exerciseSquat);
    linkExercises(sportBaseJump, exerciseSquat, exerciseGainage);
    linkExercises(sportPingPong, exerciseTravailAppuis, exerciseSprint);
    linkExercises(sportSquash, exerciseTravailAppuis, exerciseSprint);
    linkExercises(sportJudo, exerciseRandori, exerciseGainage, exerciseSquat);
    linkExercises(sportTaekwondo, exerciseKata, exerciseSautTechnique, exerciseTravailAppuis);
    linkExercises(sportKarate, exerciseKata, exerciseShadowBoxing, exerciseTravailAppuis);
    linkExercises(sportBoxe, exerciseShadowBoxing, exerciseTravailAppuis, exerciseGainage);
    linkExercises(sportEscrime, exerciseTravailAppuis, exerciseGainage);
    linkExercises(sportLutte, exerciseRandori, exerciseGainage, exerciseSquat);
    linkExercises(sportSki, exerciseGlisse, exerciseSquat, exerciseGainage);
    linkExercises(sportCurling, exerciseTirPrecision, exerciseSquat);
    linkExercises(sportHockey, exerciseGlisse, exerciseDribble, exerciseSprint);
    linkExercises(sportLuge, exerciseGlisse, exerciseGainage);
    linkExercises(sportPatinage, exerciseGlisse, exerciseEquilibre);
    linkExercises(sportBobsleigh, exerciseSprint, exerciseGainage);
    linkExercises(sportSkate, exerciseEquilibre, exerciseSautTechnique);
    linkExercises(sportTirSportif, exerciseTirPrecision, exerciseGainage);
    linkExercises(sportTirArc, exerciseTirPrecision, exerciseGainage);
    linkExercises(sportTirCible, exerciseTirPrecision, exerciseGainage);
    linkExercises(sportRepassageExtrem, exerciseMarcheSentier, exerciseEquilibre);
    linkExercises(sportGymnastique, exerciseYogaFlow, exerciseEquilibre, exerciseGainage);
    linkExercises(
        sportPentathlon,
        exerciseCourseContinue,
        exerciseNageLibre,
        exerciseTirPrecision,
        exerciseTravailAppuis);
    linkExercises(sportTriathlon, exerciseCourseContinue, exerciseNageLibre, exercisePedalage);
    linkExercises(sportFormule1, exercisePilotage, exerciseGainage);
    linkExercises(sportMotocyclisme, exercisePilotage, exerciseGainage);
    linkExercises(sportAviron, exerciseRame, exerciseGainage);
    linkExercises(sportCanoeKayak, exercisePagaie, exerciseGainage);
    linkExercises(sportSurf, exerciseEquilibre, exerciseNageLibre);
    linkExercises(sportVoile, exerciseEquilibre, exerciseGainage);
    linkExercises(sportEquitation, exerciseEquilibre, exerciseGainage);
    linkExercises(sportSpeleologie, exerciseMarcheSentier, exerciseEscaladeBloc, exerciseGainage);

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
            exerciseMarcheSentier,
            exerciseSautTechnique,
            exerciseLancerMedecineBall,
            exerciseShadowBoxing,
            exerciseTravailAppuis,
            exerciseKata,
            exerciseRandori,
            exerciseGlisse,
            exerciseTirPrecision,
            exerciseGainage,
            exerciseRame,
            exercisePagaie,
            exerciseEquilibre,
            exercisePilotage));

    sportRepository.saveAll(allSports);

    // Badges (axes sur certains sports)
    List<Badge> demoBadges =
        badgeRepository.saveAll(
            List.of(
                createSportBadge(
                    sportCourse,
                    "Rookie 5K",
                    "Reussir 5 km cumules en " + sportCourse.getName() + ".",
                    "/images/badge/running_5km.png"),
                createSportBadge(
                    sportCourse,
                    "Marathonien en Herbe",
                    "Cumuler 42 km en " + sportCourse.getName() + ".",
                    "/images/badge/running_42km.png"),
                createSportBadge(
                    sportCourse,
                    "Marathonien",
                    "Cumuler 42 km en " + sportCourse.getName() + " en moins de 4h.",
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
                    sportEscalade,
                    "Bloc Determination",
                    "Valider 5 sessions de " + sportEscalade.getName() + ".",
                    "/images/badge/escalade.png"),
                createSportBadge(
                    sportYoga,
                    "Souplesse Focus",
                    "Maintenir 20 minutes de " + sportYoga.getName() + " sans interruption.",
                    "/images/badge/yoga.png"),
                createSportBadge(
                    sportMusculation,
                    "Force Reguliere",
                    "Terminer 4 seances de " + sportMusculation.getName() + " dans le mois.",
                    "/images/badge/yoga.png"),
                createSportBadge(
                    sportBasketball,
                    "Adresse Collective",
                    "Cumuler 90 minutes de " + sportBasketball.getName() + " avec ballon.",
                    "/images/badge/running_5km.png"),
                createSportBadge(
                    sportFootball,
                    "Pressing Continu",
                    "Cumuler 10 km d'efforts en " + sportFootball.getName() + ".",
                    "/images/badge/running_42km.png"),
                createSportBadge(
                    sportParkour,
                    "Traceur Urbain",
                    "Valider 3 parcours de " + sportParkour.getName() + ".",
                    "/images/badge/escalade.png"),
                createSportBadge(
                    sportRandonnee,
                    "Grand Air",
                    "Completer une sortie de " + sportRandonnee.getName() + " de 10 km.",
                    "/images/badge/cyclisme.png"),
                createSportBadge(
                    sportPlongee,
                    "Respiration Calme",
                    "Cumuler 60 minutes de " + sportPlongee.getName() + ".",
                    "/images/badge/natation.png"),
                createSportBadge(
                    sportCourse,
                    "Cardio Tenace",
                    "Terminer 5 circuits complets de " + sportCourse.getName() + ".",
                    "/images/badge/running_5km.png"),
                createSportBadge(
                    sportTennis,
                    "Echanges Longs",
                    "Tenir 45 minutes de " + sportTennis.getName() + " sans abandonner.",
                    "/images/badge/running_42km.png")));
    assignDemoBadgeIcons(demoBadges);
    Badge badgeRookie5k = demoBadges.get(0);
    Badge badgeMarathonHerbe = demoBadges.get(1);
    Badge badgeMarathonien = demoBadges.get(2);
    Badge badgeNatation = demoBadges.get(3);
    Badge badgeCyclisme = demoBadges.get(4);
    Badge badgeEscalade = demoBadges.get(5);
    Badge badgeYoga = demoBadges.get(6);
    Badge badgeMusculation = demoBadges.get(7);
    Badge badgeBasketball = demoBadges.get(8);
    Badge badgeFootball = demoBadges.get(9);
    Badge badgeParkour = demoBadges.get(10);
    Badge badgeRandonnee = demoBadges.get(11);
    Badge badgePlongee = demoBadges.get(12);
    Badge badgeCardio = demoBadges.get(13);
    Badge badgeTennis = demoBadges.get(14);

    LocalDate today = LocalDate.now();
    // Challenges (axes sur certains sports)
    Challenge challengeRunning25k =
        createSportChallenge(
            sportCourse,
            "Challenge Running 25K",
            "Cumuler 25 km en " + sportCourse.getName() + " sur la periode.",
            ChallengeType.DISTANCE,
            25.0,
            today.minusDays(5),
            today.plusDays(21),
            userAdmin);
    challengeRunning25k.getBadges().addAll(List.of(badgeRookie5k, badgeMarathonHerbe));
    addParticipants(
        challengeRunning25k, userAlice, userJudy, userNick, userHiccup, userFishlegs, userOwen);

    Challenge challengeNatationEndurance =
        createSportChallenge(
            sportNatation,
            "Challenge Natation Endurance",
            "Cumuler 180 minutes de " + sportNatation.getName() + ".",
            ChallengeType.DUREE,
            180.0,
            today.minusDays(3),
            today.plusDays(18),
            userAdmin);
    challengeNatationEndurance.getBadges().add(badgeNatation);
    addParticipants(
        challengeNatationEndurance, userNick, userCappy, userBellwether, userStoick, userFender);

    Challenge challengeCyclisme80k =
        createSportChallenge(
            sportCyclisme,
            "Challenge Cyclisme 80K",
            "Atteindre 80 km en " + sportCyclisme.getName() + ".",
            ChallengeType.DISTANCE,
            80.0,
            today.minusDays(7),
            today.plusDays(28),
            userAdmin);
    challengeCyclisme80k.getBadges().add(badgeCyclisme);
    addParticipants(challengeCyclisme80k, userStoick, userRodney, userBigweld, userBenoit);

    Challenge challengeEscaladeVitesse =
        createSportChallenge(
            sportEscalade,
            "Challenge Escalade Vitesse",
            "Cumuler 1500 calories sur des seances de " + sportEscalade.getName() + ".",
            ChallengeType.CALORIE,
            1500.0,
            today.minusDays(2),
            today.plusDays(20),
            userAdmin);
    challengeEscaladeVitesse.getBadges().addAll(List.of(badgeEscalade, badgeMarathonien));
    addParticipants(
        challengeEscaladeVitesse, userAstrid, userBogo, userFishlegs, userBigweld, userTaiLung);

    Challenge challengeMusculationRegulier =
        createSportChallenge(
            sportMusculation,
            "Challenge Musculation Regulier",
            "Enregistrer 12 seances de " + sportMusculation.getName() + ".",
            ChallengeType.REPETITION,
            12.0,
            today.minusDays(1),
            today.plusDays(30),
            userAdmin);
    challengeMusculationRegulier.getBadges().addAll(List.of(badgeMusculation, badgeCardio));
    addParticipants(
        challengeMusculationRegulier,
        userBogo,
        userStoick,
        userShifu,
        userPo,
        userTaiLung,
        userAdmin);

    Challenge challengeBasketEquipe =
        createSportChallenge(
            sportBasketball,
            "Challenge Basket Equipe",
            "Cumuler 240 minutes de " + sportBasketball.getName() + " en collectif.",
            ChallengeType.DUREE,
            240.0,
            today.minusDays(4),
            today.plusDays(17),
            userAdmin);
    challengeBasketEquipe.getBadges().addAll(List.of(badgeBasketball, badgeCardio));
    addParticipants(challengeBasketEquipe, userFishlegs, userAlice, userBenoit, userPo, userNick);

    Challenge challengeParkourUrbain =
        createSportChallenge(
            sportParkour,
            "Challenge Traceurs Urbains",
            "Valider 6 parcours techniques de " + sportParkour.getName() + ".",
            ChallengeType.REPETITION,
            6.0,
            today.minusDays(6),
            today.plusDays(24),
            userAdmin);
    challengeParkourUrbain.getBadges().addAll(List.of(badgeParkour, badgeEscalade));
    addParticipants(challengeParkourUrbain, userRodney, userFender, userJudy, userHiccup);

    Challenge challengeRandoGrandAir =
        createSportChallenge(
            sportRandonnee,
            "Challenge Grand Air",
            "Cumuler 35 km de " + sportRandonnee.getName() + " avant la fin du mois.",
            ChallengeType.DISTANCE,
            35.0,
            today.minusDays(8),
            today.plusDays(22),
            userAdmin);
    challengeRandoGrandAir.getBadges().addAll(List.of(badgeRandonnee, badgeCyclisme));
    addParticipants(challengeRandoGrandAir, userBigweld, userOogway, userShifu, userBellwether);

    Challenge challengeFootballPressing =
        createSportChallenge(
            sportFootball,
            "Challenge Pressing",
            "Accumuler 18 km d'efforts en " + sportFootball.getName() + ".",
            ChallengeType.DISTANCE,
            18.0,
            today.minusDays(3),
            today.plusDays(19),
            userAdmin);
    challengeFootballPressing.getBadges().addAll(List.of(badgeFootball, badgeRookie5k));
    addParticipants(challengeFootballPressing, userAstrid, userOwen, userBenoit, userHiccup);

    Challenge challengeRespiration =
        createSportChallenge(
            sportPlongee,
            "Challenge Respiration Calme",
            "Cumuler 120 minutes de " + sportPlongee.getName() + " ou nage encadree.",
            ChallengeType.DUREE,
            120.0,
            today.minusDays(2),
            today.plusDays(26),
            userAdmin);
    challengeRespiration.getBadges().addAll(List.of(badgePlongee, badgeNatation));
    addParticipants(challengeRespiration, userStoick, userFender, userCappy, userOwen);

    challengeRepository.saveAll(
        List.of(
            challengeRunning25k,
            challengeNatationEndurance,
            challengeCyclisme80k,
            challengeEscaladeVitesse,
            challengeMusculationRegulier,
            challengeBasketEquipe,
            challengeParkourUrbain,
            challengeRandoGrandAir,
            challengeFootballPressing,
            challengeRespiration));

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
    Goal goalBenoitCourse =
        createGoal("Objectif reprise Benoit", GoalType.DISTANCE, 35.0, 12.0, "km", userBenoit);
    Goal goalOwenFootball =
        createGoal("Objectif pressing Owen", GoalType.DISTANCE, 24.0, 9.0, "km", userOwen);
    Goal goalAdminCardio =
        createGoal("Objectif demo admin", GoalType.DUREE, 180.0, 45.0, "min", userAdmin);
    Goal goalJudyAgilite =
        createGoal("Objectif agilite Judy", GoalType.REPETITIONS, 80.0, 36.0, "reps", userJudy);
    Goal goalBellwetherMobilite =
        createGoal("Objectif mobilite Dawn", GoalType.DUREE, 150.0, 40.0, "min", userBellwether);
    Goal goalHiccupEndurance =
        createGoal("Objectif endurance Hiccup", GoalType.DISTANCE, 45.0, 16.0, "km", userHiccup);
    Goal goalFishlegsBloc =
        createGoal(
            "Objectif bloc Fishlegs", GoalType.CALORIES, 1200.0, 410.0, "kcal", userFishlegs);
    Goal goalRodneyParkour =
        createGoal("Objectif parkour Rodney", GoalType.REPETITIONS, 10.0, 4.0, "runs", userRodney);
    Goal goalCappyYoga =
        createGoal("Objectif yoga Cappy", GoalType.DUREE, 210.0, 75.0, "min", userCappy);
    Goal goalFenderPlongee =
        createGoal("Objectif plongee Fender", GoalType.DUREE, 120.0, 35.0, "min", userFender);
    Goal goalBigweldRando =
        createGoal("Objectif rando Bigweld", GoalType.DISTANCE, 40.0, 12.0, "km", userBigweld);
    Goal goalShifuForce =
        createGoal(
            "Objectif precision Shifu", GoalType.REPETITIONS, 220.0, 90.0, "reps", userShifu);
    Goal goalOogwayRando =
        createGoal("Objectif grand air Oogway", GoalType.DISTANCE, 25.0, 8.0, "km", userOogway);
    Goal goalPoCardio =
        createGoal("Objectif cardio Po", GoalType.CALORIES, 1600.0, 520.0, "kcal", userPo);
    Goal goalTaiLungForce =
        createGoal(
            "Objectif puissance Tai Lung", GoalType.REPETITIONS, 320.0, 140.0, "reps", userTaiLung);

    List<Goal> seededGoals =
        goalRepository.saveAll(
            List.of(
                goalAliceCourse,
                goalNickNatation,
                goalAstridEscalade,
                goalStoickCyclisme,
                goalBogoMusculation,
                goalBenoitCourse,
                goalOwenFootball,
                goalAdminCardio,
                goalJudyAgilite,
                goalBellwetherMobilite,
                goalHiccupEndurance,
                goalFishlegsBloc,
                goalRodneyParkour,
                goalCappyYoga,
                goalFenderPlongee,
                goalBigweldRando,
                goalShifuForce,
                goalOogwayRando,
                goalPoCardio,
                goalTaiLungForce));

    // Attribution badges + goals aux profils de demo
    userAlice.getBadges().addAll(List.of(badgeRookie5k, badgeMarathonHerbe));
    userAlice.getGoals().add(seededGoals.get(0));

    userBenoit.getBadges().addAll(List.of(badgeRookie5k, badgeBasketball));
    userBenoit.getGoals().add(seededGoals.get(5));

    userOwen.getBadges().addAll(List.of(badgeFootball, badgeRookie5k));
    userOwen.getGoals().add(seededGoals.get(6));

    userAdmin.getBadges().addAll(List.of(badgeCardio, badgeMusculation, badgeYoga));
    userAdmin.getGoals().add(seededGoals.get(7));

    userJudy.getBadges().addAll(List.of(badgeParkour, badgeRookie5k));
    userJudy.getGoals().add(seededGoals.get(8));

    userNick.getBadges().add(badgeNatation);
    userNick.getGoals().add(seededGoals.get(1));

    userBellwether.getBadges().addAll(List.of(badgeYoga, badgeNatation));
    userBellwether.getGoals().add(seededGoals.get(9));

    userHiccup.getBadges().addAll(List.of(badgeRookie5k, badgeParkour));
    userHiccup.getGoals().add(seededGoals.get(10));

    userAstrid.getBadges().addAll(List.of(badgeEscalade, badgeMarathonien));
    userAstrid.getGoals().add(seededGoals.get(2));

    userStoick.getBadges().add(badgeCyclisme);
    userStoick.getGoals().add(seededGoals.get(3));

    userFishlegs.getBadges().addAll(List.of(badgeEscalade, badgeBasketball));
    userFishlegs.getGoals().add(seededGoals.get(11));

    userRodney.getBadges().addAll(List.of(badgeParkour, badgeCyclisme));
    userRodney.getGoals().add(seededGoals.get(12));

    userCappy.getBadges().addAll(List.of(badgeYoga, badgePlongee));
    userCappy.getGoals().add(seededGoals.get(13));

    userFender.getBadges().addAll(List.of(badgePlongee, badgeParkour));
    userFender.getGoals().add(seededGoals.get(14));

    userBigweld.getBadges().addAll(List.of(badgeRandonnee, badgeEscalade));
    userBigweld.getGoals().add(seededGoals.get(15));

    userShifu.getBadges().addAll(List.of(badgeMusculation, badgeYoga));
    userShifu.getGoals().add(seededGoals.get(16));

    userOogway.getBadges().addAll(List.of(badgeRandonnee, badgeYoga));
    userOogway.getGoals().add(seededGoals.get(17));

    userPo.getBadges().addAll(List.of(badgeCardio, badgeBasketball));
    userPo.getGoals().add(seededGoals.get(18));

    userTaiLung.getBadges().addAll(List.of(badgeMusculation, badgeEscalade));
    userTaiLung.getGoals().add(seededGoals.get(19));

    userBogo.getBadges().add(badgeYoga);
    userBogo.getGoals().add(seededGoals.get(4));

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
            sportCourse,
            userJudy);
    workoutKudo.addKudo(userTaiLung);
    workoutKudo.addKudo(userShifu);
    workoutKudo.addKudo(userOogway);
    workoutKudo.addKudo(userAlice);
    workoutKudo.addKudo(userBenoit);
    workoutKudo.addKudo(userOwen);
    workoutKudo.addKudo(userHiccup);

    // Workouts
    List<Workout> demoWorkouts =
        new ArrayList<>(
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
                    sportCourse,
                    userJudy),
                createWorkout(
                    "Parcours agilité",
                    LocalDateTime.of(2026, 4, 3, 10, 0),
                    "Toulouse",
                    cloudy,
                    List.of(
                        createWorkoutExercise(
                            155.0, 800.0, 900.0, null, null, null, null, exerciseSprint),
                        createWorkoutExercise(
                            160.0, null, 600.0, 12, 4, null, null, exerciseBurpees)),
                    sportParkour,
                    userJudy),
                createWorkout(
                    null,
                    LocalDateTime.of(2026, 4, 6, 10, 0),
                    "Toulouse",
                    clearsky,
                    new ArrayList<>(),
                    sportCourse,
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
                    sportCourse,
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
                    sportCourse,
                    userNick),
                createWorkout(
                    null,
                    LocalDateTime.of(2026, 4, 1, 10, 0),
                    "Toulouse",
                    null,
                    List.of(
                        createWorkoutExercise(
                            155.0, null, 900.0, 15, 4, null, null, exerciseBurpees),
                        createWorkoutExercise(
                            145.0, null, 600.0, 20, 4, null, null, exercisePompes)),
                    sportCourse,
                    userBogo),
                createWorkout(
                    null,
                    LocalDateTime.of(2026, 4, 4, 10, 0),
                    "Toulouse",
                    null,
                    List.of(
                        createWorkoutExercise(130.0, null, 720.0, 12, 4, null, null, exerciseSquat),
                        createWorkoutExercise(
                            140.0, null, 600.0, 8, 4, null, null, exerciseTractions)),
                    sportCallisthenie,
                    userBogo),
                createWorkout(
                    null,
                    LocalDateTime.of(2026, 4, 9, 10, 0),
                    "Toulouse",
                    null,
                    List.of(
                        createWorkoutExercise(
                            160.0, 120.0, 900.0, null, null, null, null, exerciseVoieVitesse),
                        createWorkoutExercise(
                            145.0, null, 600.0, 8, 3, null, null, exerciseTractions)),
                    sportEscalade,
                    userBogo),
                createWorkout(
                    null,
                    LocalDateTime.of(2026, 4, 2, 10, 0),
                    "Toulouse",
                    null,
                    new ArrayList<>(),
                    sportAlpinisme,
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
                    sportCourse,
                    userBellwether),
                createWorkout(
                    null,
                    LocalDateTime.of(2026, 4, 1, 10, 0),
                    "Toulouse",
                    clearsky,
                    new ArrayList<>(),
                    sportCourse,
                    userHiccup),
                createWorkout(
                    null,
                    LocalDateTime.of(2026, 4, 4, 10, 0),
                    "Toulouse",
                    null,
                    new ArrayList<>(),
                    sportCourse,
                    userHiccup),
                createWorkout(
                    null,
                    LocalDateTime.of(2026, 4, 11, 10, 0),
                    "Pau",
                    null,
                    new ArrayList<>(),
                    sportCourse,
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
                    sportEscalade,
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
                        createWorkoutExercise(
                            125.0, null, 900.0, 12, 4, 90000.0, null, exerciseSquat)),
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
                        createWorkoutExercise(
                            135.0, null, 600.0, 6, 4, null, null, exerciseTractions)),
                    sportEscalade,
                    userFishlegs),
                createWorkout(
                    null,
                    LocalDateTime.of(2026, 4, 14, 10, 0),
                    "Paris",
                    null,
                    new ArrayList<>(),
                    sportCourse,
                    userFishlegs),
                createWorkout(
                    null,
                    LocalDateTime.of(2026, 4, 3, 10, 0),
                    "Toulouse",
                    null,
                    new ArrayList<>(),
                    sportParkour,
                    userRodney),
                createWorkout(
                    null,
                    LocalDateTime.of(2026, 4, 9, 10, 0),
                    "Toulouse",
                    null,
                    new ArrayList<>(),
                    sportCourse,
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
                    sportCourse,
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
                    sportYoga,
                    userCappy),
                createWorkout(
                    null,
                    LocalDateTime.of(2026, 4, 5, 10, 0),
                    "Toulouse",
                    null,
                    new ArrayList<>(),
                    sportCourse,
                    userFender),
                createWorkout(
                    null,
                    LocalDateTime.of(2026, 4, 11, 10, 0),
                    "Toulouse",
                    null,
                    new ArrayList<>(),
                    sportParkour,
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
                    sportCourse,
                    userBigweld),
                createWorkout(
                    null,
                    LocalDateTime.of(2026, 4, 12, 10, 0),
                    "Toulouse",
                    null,
                    new ArrayList<>(),
                    sportEscalade,
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
                    userBigweld),
                createWorkout(
                    "Sortie reprise Alice",
                    LocalDateTime.of(2026, 4, 19, 9, 30),
                    "Toulouse",
                    clearsky,
                    List.of(
                        createWorkoutExercise(
                            142.0, 5200.0, 2100.0, null, null, null, null, exerciseCourseContinue),
                        createWorkoutExercise(
                            150.0, null, 420.0, 12, 3, null, null, exerciseSquat)),
                    sportCourse,
                    userAlice),
                createWorkout(
                    "Fractionne de Benoit",
                    LocalDateTime.of(2026, 4, 19, 18, 0),
                    "Toulouse",
                    cloudy,
                    List.of(
                        createWorkoutExercise(
                            168.0, 1800.0, 720.0, null, null, null, null, exerciseSprint),
                        createWorkoutExercise(
                            158.0, 2400.0, 1050.0, null, null, null, null, exerciseCourseContinue)),
                    sportCourse,
                    userBenoit),
                createWorkout(
                    "Pressing du soir",
                    LocalDateTime.of(2026, 4, 20, 19, 15),
                    "Bordeaux",
                    cloudy,
                    List.of(
                        createWorkoutExercise(
                            152.0, 3600.0, 2100.0, null, null, null, null, exerciseDribble),
                        createWorkoutExercise(
                            170.0, 900.0, 360.0, null, null, null, null, exerciseSprint)),
                    sportFootball,
                    userOwen),
                createWorkout(
                    "Validation demo admin",
                    LocalDateTime.of(2026, 4, 18, 8, 0),
                    "Toulouse",
                    clearsky,
                    List.of(
                        createWorkoutExercise(
                            145.0, null, 900.0, 14, 4, null, null, exerciseBurpees),
                        createWorkoutExercise(
                            132.0, null, 720.0, 16, 4, null, null, exercisePompes)),
                    sportCourse,
                    userAdmin),
                createWorkout(
                    "Mobilite douce",
                    LocalDateTime.of(2026, 4, 18, 11, 0),
                    "Toulouse",
                    null,
                    List.of(
                        createWorkoutExercise(
                            92.0, null, 1800.0, null, null, null, null, exerciseYogaFlow),
                        createWorkoutExercise(
                            105.0, null, 480.0, 10, 3, null, null, exerciseSquat)),
                    sportCourse,
                    userBellwether),
                createWorkout(
                    "Endurance mixte Berk",
                    LocalDateTime.of(2026, 4, 18, 16, 0),
                    "Pau",
                    rain,
                    List.of(
                        createWorkoutExercise(
                            150.0, 4200.0, 1800.0, null, null, null, null, exerciseCourseContinue),
                        createWorkoutExercise(
                            158.0, null, 600.0, 10, 3, null, null, exerciseBurpees)),
                    sportCourse,
                    userHiccup),
                createWorkout(
                    "Grimpe technique",
                    LocalDateTime.of(2026, 4, 19, 10, 30),
                    "Tarbes",
                    null,
                    List.of(
                        createWorkoutExercise(
                            148.0, 95.0, 1350.0, null, null, null, null, exerciseVoieVitesse),
                        createWorkoutExercise(
                            142.0, null, 720.0, 7, 4, null, null, exerciseTractions)),
                    sportEscalade,
                    userAstrid),
                createWorkout(
                    "Trace urbaine Rodney",
                    LocalDateTime.of(2026, 4, 19, 17, 0),
                    "Toulouse",
                    clearsky,
                    List.of(
                        createWorkoutExercise(
                            162.0, 1300.0, 780.0, null, null, null, null, exerciseSprint),
                        createWorkoutExercise(
                            150.0, null, 600.0, 9, 4, null, null, exerciseTractions)),
                    sportParkour,
                    userRodney),
                createWorkout(
                    "Plongee controlee",
                    LocalDateTime.of(2026, 4, 20, 14, 0),
                    "Marseille",
                    null,
                    List.of(
                        createWorkoutExercise(
                            118.0, 900.0, 2100.0, null, null, null, null, exerciseNageLibre),
                        createWorkoutExercise(
                            122.0, 700.0, 1500.0, null, null, null, null, exerciseCrawl)),
                    sportPlongee,
                    userFender),
                createWorkout(
                    "Precision Shifu",
                    LocalDateTime.of(2026, 4, 18, 7, 30),
                    "Toulouse",
                    null,
                    List.of(
                        createWorkoutExercise(
                            112.0, null, 900.0, 15, 5, null, null, exercisePompes),
                        createWorkoutExercise(
                            118.0, null, 900.0, 12, 5, null, null, exerciseSquat)),
                    sportCourse,
                    userShifu),
                createWorkout(
                    "Sentier tranquille",
                    LocalDateTime.of(2026, 4, 19, 8, 15),
                    "Foix",
                    clearsky,
                    List.of(
                        createWorkoutExercise(
                            98.0, 8200.0, 4200.0, null, null, null, null, exerciseMarcheSentier),
                        createWorkoutExercise(
                            88.0, null, 900.0, null, null, null, null, exerciseYogaFlow)),
                    sportRandonnee,
                    userOogway),
                createWorkout(
                    "Circuit dragon warrior",
                    LocalDateTime.of(2026, 4, 20, 12, 30),
                    "Toulouse",
                    cloudy,
                    List.of(
                        createWorkoutExercise(
                            152.0, null, 840.0, 12, 5, null, null, exerciseBurpees),
                        createWorkoutExercise(
                            138.0, null, 780.0, 10, 5, null, null, exercisePompes),
                        createWorkoutExercise(
                            145.0, null, 900.0, 14, 4, null, null, exerciseSquat)),
                    sportCourse,
                    userPo),
                createWorkout(
                    "Force explosive Tai Lung",
                    LocalDateTime.of(2026, 4, 20, 6, 45),
                    "Toulouse",
                    null,
                    List.of(
                        createWorkoutExercise(
                            135.0, null, 900.0, 6, 5, 95000.0, null, exerciseDeveloppeCouche),
                        createWorkoutExercise(
                            142.0, null, 780.0, 8, 5, null, null, exerciseTractions),
                        createWorkoutExercise(
                            148.0, null, 900.0, 10, 5, 110000.0, null, exerciseSquat)),
                    sportMusculation,
                    userTaiLung)));
    enrichDemoWorkoutInteractions(
        demoWorkouts,
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
    workoutRepository.saveAll(demoWorkouts);

    List<Workout> coverageWorkouts = createCoverageWorkouts(allSports, userAdmin, cloudy);
    enrichDemoWorkoutInteractions(
        coverageWorkouts,
        List.of(userAlice, userBenoit, userOwen, userJudy, userNick, userAstrid, userRodney));
    workoutRepository.saveAll(coverageWorkouts);
  }

  private Workout createWorkout(
      String name,
      LocalDateTime date,
      String address,
      WeatherStatsDTO weather,
      List<WorkoutExercise> exercises,
      Sport sport,
      User user) {
    if (sport == null) {
      throw new IllegalArgumentException("Un workout de demo doit toujours avoir un sport.");
    }
    String resolvedName = name == null || name.isBlank() ? "Seance demo " + sport.getName() : name;
    String resolvedAddress = address == null || address.isBlank() ? "Toulouse" : address;
    WeatherStatsDTO resolvedWeather =
        weather == null
            ? new WeatherStatsDTO("20", "21", "18", "19", "0.00", "9", "cloudy")
            : weather;
    if (exercises == null || exercises.isEmpty()) {
      List<Exercise> sportExercises = sport.getExercises();
      if (sportExercises == null || sportExercises.isEmpty()) {
        throw new IllegalStateException(
            "Le sport " + sport.getName() + " doit avoir au moins un exercice.");
      }
      exercises =
          List.of(
              createWorkoutExercise(
                  120.0, null, 900.0, 12, 3, null, null, sportExercises.getFirst()));
    }

    Workout workout =
        new Workout(resolvedName, date, resolvedAddress, resolvedWeather, exercises, sport, user);
    for (WorkoutExercise exercise : exercises) {
      exercise.setWorkout(workout);
    }
    return workout;
  }

  private Sport createSport(SportName name, Double met) {
    return new Sport(name, met);
  }

  private List<Workout> createCoverageWorkouts(
      List<Sport> sports, User user, WeatherStatsDTO weather) {
    List<Workout> workouts = new ArrayList<>();
    LocalDateTime startDate = LocalDateTime.of(2026, 4, 21, 9, 0);

    for (int index = 0; index < sports.size(); index++) {
      Sport sport = sports.get(index);
      if (sport == null) {
        continue;
      }
      List<Exercise> exercises = sport.getExercises();
      if (exercises == null || exercises.size() < 2) {
        throw new IllegalStateException(
            "Le sport " + sport.getName() + " doit avoir au moins 2 exercices.");
      }

      workouts.add(
          createWorkout(
              "Seance demo " + sport.getName(),
              startDate.plusDays(index),
              "Toulouse",
              weather,
              List.of(
                  createWorkoutExercise(120.0, null, 900.0, 12, 3, null, null, exercises.get(0)),
                  createWorkoutExercise(125.0, null, 900.0, 10, 3, null, null, exercises.get(1))),
              sport,
              user));
    }

    return workouts;
  }

  private void enrichDemoWorkoutInteractions(List<Workout> workouts, List<User> users) {
    if (workouts == null || users == null || users.isEmpty()) {
      return;
    }

    String[] comments = {
      "Belle seance, le rythme est propre.",
      "Solide effort, ca donne envie de suivre.",
      "Bonne progression sur cette activite.",
      "Joli volume, continue comme ca.",
      "Seance bien construite."
    };

    for (int index = 0; index < workouts.size(); index++) {
      Workout workout = workouts.get(index);
      if (workout == null) {
        continue;
      }

      User firstKudo = users.get(index % users.size());
      User secondKudo = users.get((index + 3) % users.size());
      if (workout.getUser() == null || !firstKudo.getId().equals(workout.getUser().getId())) {
        workout.addKudo(firstKudo);
      }
      if (workout.getUser() == null || !secondKudo.getId().equals(workout.getUser().getId())) {
        workout.addKudo(secondKudo);
      }

      User commentAuthor = users.get((index + 5) % users.size());
      if (workout.getUser() != null && commentAuthor.getId().equals(workout.getUser().getId())) {
        commentAuthor = users.get((index + 6) % users.size());
      }
      workout.addComment(new Comment(comments[index % comments.length], workout, commentAuthor));

      if (index % 3 == 0) {
        User secondCommentAuthor = users.get((index + 9) % users.size());
        workout.addComment(
            new Comment(comments[(index + 2) % comments.length], workout, secondCommentAuthor));
      }
    }
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
    if (exercise == null) {
      throw new IllegalArgumentException("Un WorkoutExercise de demo doit avoir un exercice.");
    }
    return new WorkoutExercise(
        averageBps == null ? 120.0 : averageBps,
        distanceM == null ? 0.0 : distanceM,
        durationSec == null ? 900.0 : durationSec,
        reps == null ? 1 : reps,
        sets == null ? 1 : sets,
        weightG == null ? 0.0 : weightG,
        workout,
        exercise);
  }

  private Exercise createExercice(String name, Double calPerSec, List<Sport> sports) {
    return new Exercise(name, calPerSec, sports);
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

  private void addParticipants(Challenge challenge, User... participants) {
    for (User participant : participants) {
      if (participant != null && !challenge.getParticipants().contains(participant)) {
        challenge.getParticipants().add(participant);
      }
    }
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
