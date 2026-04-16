package web.sportflow.workout;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.sportflow.friendship.FriendshipService;
import web.sportflow.notification.NotificationService;
import web.sportflow.user.User;
import web.sportflow.weather.WeatherService;
import web.sportflow.weather.WeatherStatsDTO;
import web.sportflow.workout.statistique.MonthlyBarView;

@Service
public class WorkoutService {

  private final WorkoutRepository workoutRepository;
  private final WeatherService weatherService;
  private final FriendshipService friendshipService;
  private final NotificationService notificationService;

  public WorkoutService(
      WorkoutRepository workoutRepository,
      WeatherService weatherService,
      FriendshipService friendshipService,
      NotificationService notificationService) {
    this.workoutRepository = workoutRepository;
    this.weatherService = weatherService;
    this.friendshipService = friendshipService;
    this.notificationService = notificationService;
  }

  @Transactional
  public Workout createWorkout(Workout workout) {
    Workout newWorkout =
        new Workout(
            workout.getName(),
            workout.getDescription(),
            workout.getDate(),
            workout.getAddress(),
            workout.getDurationSec(),
            workout.getRating(),
            workout.getSport(),
            workout.getWeather(),
            workout.getWorkoutExercises(),
            workout.getUser());
    if (workout.getAddress() != null
        && !workout.getAddress().isEmpty()
        && workout.getDate() != null
        && workout.getDurationSec() != null
        && workout.getDate().getMonthValue() > java.time.LocalDateTime.now().getMonthValue() - 1) {
      WeatherStatsDTO weatherStatsDTO =
          weatherService.getWeatherStats(
              workout.getAddress(), workout.getDate(), workout.getDurationSec());
      newWorkout.setWeather(weatherStatsDTO);
    }
    Workout savedWorkout = workoutRepository.save(newWorkout);

    return workoutRepository.save(savedWorkout);
  }

  @Transactional(readOnly = true)
  public List<Workout> getAll() {
    List<Workout> workouts = workoutRepository.findAllByOrderByDateDesc();
    workouts.forEach(
        workout -> {
          Hibernate.initialize(workout.getWorkoutExercises());
          if (workout.getUser() != null) {
            Hibernate.initialize(workout.getUser().getBadges());
          }
        });
    return workouts;
  }

  @Transactional(readOnly = true)
  public List<Workout> getFriendsWorkout(long userId) {
    return getAllForUsers(friendshipService.getCurrentUserAndFriendIds(userId));
  }

  @Transactional(readOnly = true)
  public List<Workout> getAllForUsers(List<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return List.of();
    }
    return keepDisplayable(workoutRepository.findByUserIdInOrderByDateDesc(userIds));
  }

  @Transactional(readOnly = true)
  public List<Workout> getAllStatutsForUser() {
    return keepDisplayable(workoutRepository.findAllByOrderByDateDesc());
  }

  @Transactional(readOnly = true)
  public List<Workout> getIncompleteForUser(User user) {
    if (user == null || user.getId() == null) {
      return List.of();
    }
    return prepareWorkouts(workoutRepository.findByUserOrderByDateDesc(user)).stream()
        .filter(workout -> !isDisplayable(workout))
        .toList();
  }

  public double getTotalDistanceThisWeek(User user) {
    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);

    java.time.LocalDateTime start = startOfWeek.atStartOfDay();
    java.time.LocalDateTime end = today.plusDays(1).atStartOfDay();

    return findDisplayableByUserAndDateBetween(user, start, end).stream()
        .mapToDouble(this::getWorkoutDistanceKm)
        .sum();
  }

  public double getTotalDistanceThisMonth(User user) {
    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.LocalDate startOfMonth = today.withDayOfMonth(1);

    java.time.LocalDateTime start = startOfMonth.atStartOfDay();
    java.time.LocalDateTime end = today.plusDays(1).atStartOfDay();

    return findDisplayableByUserAndDateBetween(user, start, end).stream()
        .mapToDouble(this::getWorkoutDistanceKm)
        .sum();
  }

  public double getTotalDistanceThisYear(User user) {
    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.LocalDate startOfYear = today.withDayOfYear(1);

    java.time.LocalDateTime start = startOfYear.atStartOfDay();
    java.time.LocalDateTime end = today.plusDays(1).atStartOfDay();

    return findDisplayableByUserAndDateBetween(user, start, end).stream()
        .mapToDouble(this::getWorkoutDistanceKm)
        .sum();
  }

  public double getTotalDurationThisWeek(User user) {
    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);

    java.time.LocalDateTime start = startOfWeek.atStartOfDay();
    java.time.LocalDateTime end = today.plusDays(1).atStartOfDay();

    return findDisplayableByUserAndDateBetween(user, start, end).stream()
        .filter(workout -> workout.getDurationSec() != null)
        .mapToDouble(Workout::getDurationSec)
        .sum();
  }

  public double getTotalCaloriesThisWeek(User user) {
    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);

    java.time.LocalDateTime start = startOfWeek.atStartOfDay();
    java.time.LocalDateTime end = today.plusDays(1).atStartOfDay();

    return findDisplayableByUserAndDateBetween(user, start, end).stream()
        .mapToDouble(
            workout -> {
              Double calories = workout.getCalorieBurn();
              return calories != null ? calories : 0.0;
            })
        .sum();
  }

  // Evolution month button

  public List<Double> getMonthlyDistancesCurrentYear(User user) {
    int currentYear = LocalDate.now().getYear();
    List<Double> monthlyDistances = new ArrayList<>();

    for (int month = 1; month <= 12; month++) {
      LocalDate startOfMonth = LocalDate.of(currentYear, month, 1);
      LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

      double totalDistance =
          findDisplayableByUserAndDateBetween(
                  user, startOfMonth.atStartOfDay(), endOfMonth.plusDays(1).atStartOfDay())
              .stream()
              .mapToDouble(this::getWorkoutDistanceKm)
              .sum();

      monthlyDistances.add(Math.round(totalDistance * 10.0) / 10.0);
    }

    return monthlyDistances;
  }

  @Transactional(readOnly = true)
  public List<MonthlyBarView> getMonthlyBarViewsCurrentYear(User user) {
    List<Double> monthlyDistances = getMonthlyDistancesCurrentYear(user);

    String[] labels = {
      "Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Aoû", "Sep", "Oct", "Nov", "Déc"
    };

    double max = monthlyDistances.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
    int currentMonthIndex = LocalDate.now().getMonthValue() - 1;

    List<MonthlyBarView> bars = new ArrayList<>();

    for (int i = 0; i < monthlyDistances.size(); i++) {
      double value = monthlyDistances.get(i);

      int height;
      if (max <= 0) {
        height = 24;
      } else {
        height = (int) Math.round((value / max) * 220);
        height = Math.max(height, 24);
      }

      bars.add(new MonthlyBarView(labels[i], value, height, i == currentMonthIndex));
    }

    return bars;
  }

  public double getAverageMonthlyDistanceThisYear(User user) {
    int currentYear = LocalDate.now().getYear();
    LocalDate today = LocalDate.now();
    int currentMonth = today.getMonthValue();

    double total = 0.0;

    for (int month = 1; month <= currentMonth; month++) {
      LocalDate startOfMonth = LocalDate.of(currentYear, month, 1);
      LocalDate endOfMonth =
          (month == currentMonth)
              ? today
              : startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

      double monthDistance =
          findDisplayableByUserAndDateBetween(
                  user, startOfMonth.atStartOfDay(), endOfMonth.plusDays(1).atStartOfDay())
              .stream()
              .mapToDouble(this::getWorkoutDistanceKm)
              .sum();

      total += monthDistance;
    }

    if (currentMonth == 0) {
      return 0.0;
    }

    return total / currentMonth;
  }

  public double getDistanceGapVsAverageMonthly(User user) {
    double thisMonth = getTotalDistanceThisMonth(user);
    double average = getAverageMonthlyDistanceThisYear(user);
    return thisMonth - average;
  }

  public int getMonthlyProgressPercent(User user, double monthlyGoalKm) {
    if (monthlyGoalKm <= 0) {
      return 0;
    }

    double thisMonth = getTotalDistanceThisMonth(user);
    int percent = (int) Math.round((thisMonth / monthlyGoalKm) * 100.0);
    return Math.min(percent, 100);
  }

  public List<String> getMonthDayLabels() {
    return List.of("S1", "S2", "S3", "S4");
  }

  public List<Double> getCurrentMonthCurve(User user) {
    LocalDate today = LocalDate.now();
    LocalDate startOfMonth = today.withDayOfMonth(1);

    List<Double> curve = new ArrayList<>();

    LocalDate week1End = startOfMonth.plusDays(6);
    LocalDate week2End = startOfMonth.plusDays(13);
    LocalDate week3End = startOfMonth.plusDays(20);
    LocalDate week4End = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

    curve.add(getDistanceBetween(user, startOfMonth, minDate(week1End, today)));
    curve.add(getDistanceBetween(user, startOfMonth, minDate(week2End, today)));
    curve.add(getDistanceBetween(user, startOfMonth, minDate(week3End, today)));
    curve.add(getDistanceBetween(user, startOfMonth, minDate(week4End, today)));

    return curve.stream().map(v -> Math.round(v * 10.0) / 10.0).toList();
  }

  public List<Double> getYearAverageCurve(User user) {
    int currentYear = LocalDate.now().getYear();
    int currentMonth = LocalDate.now().getMonthValue();

    double sumS1 = 0.0;
    double sumS2 = 0.0;
    double sumS3 = 0.0;
    double sumS4 = 0.0;

    for (int month = 1; month <= currentMonth; month++) {
      LocalDate startOfMonth = LocalDate.of(currentYear, month, 1);
      LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

      LocalDate week1End = startOfMonth.plusDays(6);
      LocalDate week2End = startOfMonth.plusDays(13);
      LocalDate week3End = startOfMonth.plusDays(20);
      LocalDate week4End = endOfMonth;

      sumS1 += getDistanceBetween(user, startOfMonth, minDate(week1End, endOfMonth));
      sumS2 += getDistanceBetween(user, startOfMonth, minDate(week2End, endOfMonth));
      sumS3 += getDistanceBetween(user, startOfMonth, minDate(week3End, endOfMonth));
      sumS4 += getDistanceBetween(user, startOfMonth, minDate(week4End, endOfMonth));
    }

    if (currentMonth == 0) {
      return List.of(0.0, 0.0, 0.0, 0.0);
    }

    return List.of(
        Math.round((sumS1 / currentMonth) * 10.0) / 10.0,
        Math.round((sumS2 / currentMonth) * 10.0) / 10.0,
        Math.round((sumS3 / currentMonth) * 10.0) / 10.0,
        Math.round((sumS4 / currentMonth) * 10.0) / 10.0);
  }

  public List<String> getWeekLabels() {
    return List.of("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim");
  }

  public List<Double> getWeekDistances(User user) {
    LocalDate today = LocalDate.now();
    LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);

    List<Double> distances = new ArrayList<>();

    for (int i = 0; i < 7; i++) {
      LocalDate currentDay = startOfWeek.plusDays(i);

      double total =
          findDisplayableByUserAndDateBetween(
                  user, currentDay.atStartOfDay(), currentDay.plusDays(1).atStartOfDay())
              .stream()
              .mapToDouble(this::getWorkoutDistanceKm)
              .sum();

      distances.add(Math.round(total * 10.0) / 10.0);
    }

    return distances;
  }

  public List<String> getMonthLabelsForChart() {
    LocalDate today = LocalDate.now();
    int daysInMonth = today.lengthOfMonth();

    List<String> labels = new ArrayList<>();
    for (int day = 1; day <= daysInMonth; day++) {
      labels.add(String.valueOf(day));
    }

    return labels;
  }

  public List<Double> getMonthDistancesForChart(User user) {
    LocalDate today = LocalDate.now();
    LocalDate startOfMonth = today.withDayOfMonth(1);
    int daysInMonth = today.lengthOfMonth();

    List<Double> distances = new ArrayList<>();

    for (int day = 1; day <= daysInMonth; day++) {
      LocalDate currentDate = startOfMonth.withDayOfMonth(day);

      double total =
          findDisplayableByUserAndDateBetween(
                  user, currentDate.atStartOfDay(), currentDate.plusDays(1).atStartOfDay())
              .stream()
              .mapToDouble(this::getWorkoutDistanceKm)
              .sum();

      distances.add(Math.round(total * 10.0) / 10.0);
    }

    return distances;
  }

  public List<String> getYearLabelsForChart() {
    return List.of(
        "Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Aoû", "Sep", "Oct", "Nov", "Déc");
  }

  @Transactional(readOnly = true)
  public List<Double> getYearDistancesForChart(User user) {
    return getMonthlyDistancesCurrentYear(user);
  }

  private double getDistanceBetween(User user, LocalDate startDate, LocalDate endDate) {
    if (endDate.isBefore(startDate)) {
      return 0.0;
    }

    return findDisplayableByUserAndDateBetween(
            user, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay())
        .stream()
        .mapToDouble(this::getWorkoutDistanceKm)
        .sum();
  }

  private List<Workout> findDisplayableByUserAndDateBetween(
      User user, java.time.LocalDateTime start, java.time.LocalDateTime end) {
    if (user == null || user.getId() == null || start == null || end == null) {
      return List.of();
    }
    return keepDisplayable(workoutRepository.findByUserAndDateBetween(user, start, end));
  }

  private double getWorkoutDistanceKm(Workout workout) {
    if (workout == null || workout.getWorkoutExercises() == null) {
      return 0.0;
    }

    double totalDistanceM =
        workout.getWorkoutExercises().stream()
            .filter(exercise -> exercise != null && exercise.getDistanceM() != null)
            .mapToDouble(WorkoutExercise::getDistanceM)
            .sum();

    return totalDistanceM / 1000.0;
  }

  private LocalDate minDate(LocalDate a, LocalDate b) {
    return a.isBefore(b) ? a : b;
  }

  @Transactional(readOnly = true)
  public Optional<Workout> findById(Long id) {
    return workoutRepository
        .findById(id)
        .map(
            workout -> {
              Hibernate.initialize(workout.getWorkoutExercises());
              return workout;
            });
  }

  @Transactional
  public void toggleKudo(Long workoutId, User currentUser) {
    Workout workout =
        workoutRepository
            .findById(workoutId)
            .orElseThrow(() -> new RuntimeException("Workout non trouvé"));
    if (workout.isKudoedBy(currentUser)) {
      workout.removeKudo(currentUser);
    } else {
      workout.addKudo(currentUser);
      notificationService.notifyKudoOnWorkout(workout, currentUser);
    }

    workoutRepository.save(workout);
  }

  public Workout saveWorkout(Workout workout, User currentUser) {
    workout.setUser(currentUser);
    workout.getCalorieBurn();
    return workoutRepository.save(workout);
  }

  public boolean isDisplayable(Workout workout) {
    return workout != null && workout.isPublished() && isPublishable(workout);
  }

  public boolean isPublishable(Workout workout) {
    if (workout == null
        || workout.getDate() == null
        || workout.getUser() == null
        || workout.getSport() == null
        || workout.getDescription() == null
        || workout.getDescription().isBlank()
        || workout.getRating() == null
        || workout.getDurationSec() == null
        || workout.getDurationSec() <= 0
        || workout.getAddress() == null
        || workout.getAddress().isBlank()
        || workout.getWorkoutExercises() == null
        || workout.getWorkoutExercises().isEmpty()) {
      return false;
    }

    return workout.getWorkoutExercises().stream()
        .allMatch(
            workoutExercise ->
                workoutExercise != null
                    && workoutExercise.getExercise() != null
                    && hasExerciseMetric(workoutExercise));
  }

  private boolean hasExerciseMetric(WorkoutExercise workoutExercise) {
    return workoutExercise != null
        && (isPositive(workoutExercise.getDurationSec())
            || isPositive(workoutExercise.getDistanceM())
            || isPositive(workoutExercise.getSets())
            || isPositive(workoutExercise.getReps())
            || isPositive(workoutExercise.getWeightKg())
            || isPositive(workoutExercise.getAverageBpm())
            || isPositive(workoutExercise.getElevationGainM())
            || isPositive(workoutExercise.getMaxSpeedKmh())
            || isPositive(workoutExercise.getScore())
            || isPositive(workoutExercise.getAttempts())
            || isPositive(workoutExercise.getSuccessfulAttempts())
            || isPositive(workoutExercise.getAccuracyPercent())
            || isPositive(workoutExercise.getHeightM())
            || isPositive(workoutExercise.getDepthM())
            || isPositive(workoutExercise.getLaps())
            || isPositive(workoutExercise.getRounds()));
  }

  private boolean isPositive(Number value) {
    return value != null && value.doubleValue() > 0;
  }

  private List<Workout> keepDisplayable(List<Workout> workouts) {
    return prepareWorkouts(workouts).stream().filter(this::isDisplayable).toList();
  }

  private List<Workout> prepareWorkouts(List<Workout> workouts) {
    if (workouts == null) {
      return List.of();
    }
    workouts.forEach(
        workout -> {
          if (workout == null) {
            return;
          }
          workout.getWorkoutExercises().size();
          if (workout.getUser() != null) {
            workout.getUser().getBadges().size();
          }
        });
    return workouts;
  }

  public void deleteWorkout(Workout workout) {
    workoutRepository.delete(workout);
  }
}
