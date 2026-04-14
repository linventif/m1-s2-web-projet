package utc.miage.tp.workout;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utc.miage.tp.user.User;
import utc.miage.tp.weather.WeatherService;
import utc.miage.tp.weather.WeatherStatsDTO;
import utc.miage.tp.workout.statistique.MonthlyBarView;

@Service
public class WorkoutService {

  private final WorkoutRepository workoutRepository;
  private final WeatherService weatherService;

  public WorkoutService(WorkoutRepository workoutRepository, WeatherService weatherService) {
    this.workoutRepository = workoutRepository;
    this.weatherService = weatherService;
  }

  @Transactional
  public Workout createWorkout(Workout workout) {
    Workout newWorkout =
        new Workout(
            workout.getDate(),
            workout.getDistance(),
            workout.getDuration(),
            workout.getAddress(),
            workout.getRating(),
            workout.getSport(),
            workout.getUser());
    if (workout.getAddress() != null
        && !workout.getAddress().isEmpty()
        && workout.getDate() != null
        && workout.getDate().getMonthValue() > java.time.LocalDateTime.now().getMonthValue() - 1) {
      WeatherStatsDTO weatherStatsDTO =
          weatherService.getWeatherStats(
              workout.getAddress(), workout.getDate(), workout.getDuration());
      newWorkout.setWeather(weatherStatsDTO);
    }
    Workout savedWorkout = workoutRepository.save(newWorkout);

    return workoutRepository.save(savedWorkout);
  }

  public List<Workout> getAll() {
    return workoutRepository.findAll();
  }

  public List<Workout> getAllStatutsForUser() {
    return workoutRepository.findAll();
  }

  public double getTotalDistanceThisWeek(User user) {
    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);

    java.time.LocalDateTime start = startOfWeek.atStartOfDay();
    java.time.LocalDateTime end = today.plusDays(1).atStartOfDay();

    return workoutRepository.findByUserAndDateBetween(user, start, end).stream()
        .filter(workout -> workout.getDistance() != null)
        .mapToDouble(Workout::getDistance)
        .sum();
  }

  public double getTotalDistanceThisMonth(User user) {
    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.LocalDate startOfMonth = today.withDayOfMonth(1);

    java.time.LocalDateTime start = startOfMonth.atStartOfDay();
    java.time.LocalDateTime end = today.plusDays(1).atStartOfDay();

    return workoutRepository.findByUserAndDateBetween(user, start, end).stream()
        .filter(workout -> workout.getDistance() != null)
        .mapToDouble(Workout::getDistance)
        .sum();
  }

  public double getTotalDistanceThisYear(User user) {
    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.LocalDate startOfYear = today.withDayOfYear(1);

    java.time.LocalDateTime start = startOfYear.atStartOfDay();
    java.time.LocalDateTime end = today.plusDays(1).atStartOfDay();

    return workoutRepository.findByUserAndDateBetween(user, start, end).stream()
        .filter(workout -> workout.getDistance() != null)
        .mapToDouble(Workout::getDistance)
        .sum();
  }

  public double getTotalDurationThisWeek(User user) {
    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);

    java.time.LocalDateTime start = startOfWeek.atStartOfDay();
    java.time.LocalDateTime end = today.plusDays(1).atStartOfDay();

    return workoutRepository.findByUserAndDateBetween(user, start, end).stream()
        .filter(workout -> workout.getDuration() != null)
        .mapToDouble(Workout::getDuration)
        .sum();
  }

  public double getTotalCaloriesThisWeek(User user) {
    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);

    java.time.LocalDateTime start = startOfWeek.atStartOfDay();
    java.time.LocalDateTime end = today.plusDays(1).atStartOfDay();

    return workoutRepository.findByUserAndDateBetween(user, start, end).stream()
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
          workoutRepository
              .findByUserAndDateBetween(
                  user, startOfMonth.atStartOfDay(), endOfMonth.plusDays(1).atStartOfDay())
              .stream()
              .filter(workout -> workout.getDistance() != null)
              .mapToDouble(Workout::getDistance)
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
          workoutRepository
              .findByUserAndDateBetween(
                  user, startOfMonth.atStartOfDay(), endOfMonth.plusDays(1).atStartOfDay())
              .stream()
              .filter(workout -> workout.getDistance() != null)
              .mapToDouble(Workout::getDistance)
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
          workoutRepository
              .findByUserAndDateBetween(
                  user, currentDay.atStartOfDay(), currentDay.plusDays(1).atStartOfDay())
              .stream()
              .filter(workout -> workout.getDistance() != null)
              .mapToDouble(Workout::getDistance)
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
          workoutRepository
              .findByUserAndDateBetween(
                  user, currentDate.atStartOfDay(), currentDate.plusDays(1).atStartOfDay())
              .stream()
              .filter(workout -> workout.getDistance() != null)
              .mapToDouble(Workout::getDistance)
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

    return workoutRepository
        .findByUserAndDateBetween(
            user, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay())
        .stream()
        .filter(workout -> workout.getDistance() != null)
        .mapToDouble(Workout::getDistance)
        .sum();
  }

  private LocalDate minDate(LocalDate a, LocalDate b) {
    return a.isBefore(b) ? a : b;
  }
}
