package web.sportflow.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import web.sportflow.exercise.Exercise;
import web.sportflow.sport.Sport;
import web.sportflow.user.User;
import web.sportflow.weather.WeatherStatsDTO;
import web.sportflow.workout.Workout;
import web.sportflow.workout.WorkoutExercise;
import web.sportflow.workout.WorkoutRepository;
import web.sportflow.workout.comment.Comment;

final class ReferenceWorkoutCatalog {

  private ReferenceWorkoutCatalog() {}

  static void seed(
      WorkoutRepository workoutRepository,
      ReferenceUserCatalog.UsersDto usersDto,
      ReferenceSportExerciseCatalog.SportExercisesDto sportExercisesDto) {
    User userAlice = usersDto.user("alice");
    User userBenoit = usersDto.user("benoit");
    User userOwen = usersDto.user("owen");
    User userAdmin = usersDto.user("admin");
    User userJudy = usersDto.user("judy");
    User userNick = usersDto.user("nick");
    User userBogo = usersDto.user("bogo");
    User userBellwether = usersDto.user("bellwether");
    User userHiccup = usersDto.user("hiccup");
    User userAstrid = usersDto.user("astrid");
    User userStoick = usersDto.user("stoick");
    User userFishlegs = usersDto.user("fishlegs");
    User userRodney = usersDto.user("rodney");
    User userCappy = usersDto.user("cappy");
    User userFender = usersDto.user("fender");
    User userBigweld = usersDto.user("bigweld");
    User userShifu = usersDto.user("shifu");
    User userOogway = usersDto.user("oogway");
    User userPo = usersDto.user("po");
    User userTaiLung = usersDto.user("taiLung");

    List<Sport> allSports = sportExercisesDto.allSports();

    Sport sportAlpinisme = sportExercisesDto.sport("Alpinisme");
    Sport sportBasketball = sportExercisesDto.sport("Basketball");
    Sport sportCallisthenie = sportExercisesDto.sport("Callisthenie");
    Sport sportCourse = sportExercisesDto.sport("Course");
    Sport sportCyclisme = sportExercisesDto.sport("Cyclisme");
    Sport sportEscalade = sportExercisesDto.sport("Escalade");
    Sport sportFootball = sportExercisesDto.sport("Football");
    Sport sportMusculation = sportExercisesDto.sport("Musculation");
    Sport sportNatation = sportExercisesDto.sport("Natation");
    Sport sportParkour = sportExercisesDto.sport("Parkour");
    Sport sportPlongee = sportExercisesDto.sport("Plongee");
    Sport sportRandonnee = sportExercisesDto.sport("Randonnee");
    Sport sportSautParachute = sportExercisesDto.sport("Saut_Parachute");
    Sport sportYoga = sportExercisesDto.sport("Yoga");

    Exercise exerciseBurpees = sportExercisesDto.exercise("Burpees");
    Exercise exerciseCourseContinue = sportExercisesDto.exercise("Course continue");
    Exercise exerciseCrawl = sportExercisesDto.exercise("Crawl");
    Exercise exerciseDeveloppeCouche = sportExercisesDto.exercise("Developpe couche");
    Exercise exerciseDeveloppeMilitaire = sportExercisesDto.exercise("Developpe militaire");
    Exercise exerciseDribble = sportExercisesDto.exercise("Dribble et tirs");
    Exercise exerciseEscaladeBloc = sportExercisesDto.exercise("Escalade bloc");
    Exercise exerciseMarcheSentier = sportExercisesDto.exercise("Marche sur sentier");
    Exercise exerciseMonteeCote = sportExercisesDto.exercise("Montee de cote");
    Exercise exerciseNageLibre = sportExercisesDto.exercise("Nage libre");
    Exercise exercisePedalage = sportExercisesDto.exercise("Pedalage endurance");
    Exercise exercisePompes = sportExercisesDto.exercise("Pompes");
    Exercise exerciseSprint = sportExercisesDto.exercise("Sprint");
    Exercise exerciseSquat = sportExercisesDto.exercise("Squat");
    Exercise exerciseTractions = sportExercisesDto.exercise("Tractions");
    Exercise exerciseVoieVitesse = sportExercisesDto.exercise("Voie de vitesse");
    Exercise exerciseYogaFlow = sportExercisesDto.exercise("Yoga flow");

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

  private static Workout createWorkout(
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
    for (int index = 0; index < exercises.size(); index++) {
      WorkoutExercise exercise = exercises.get(index);
      exercise.setWorkout(workout);
      ReferenceWorkoutExerciseMetrics.enrich(exercise, sport, index);
    }
    return workout;
  }

  private static List<Workout> createCoverageWorkouts(
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

  private static void enrichDemoWorkoutInteractions(List<Workout> workouts, List<User> users) {
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

  private static WorkoutExercise createWorkoutExercise(
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
}
