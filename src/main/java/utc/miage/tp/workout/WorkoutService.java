package utc.miage.tp.workout;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utc.miage.tp.user.User;
import utc.miage.tp.weather.WeatherService;
import utc.miage.tp.weather.WeatherStatsDTO;

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

  @Transactional(readOnly = true)
  public List<Workout> getAll() {
    return workoutRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Workout> getAllStatutsForUser(User user) {
    return workoutRepository.findAll();
  }

  @Transactional(readOnly = true)
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

  @Transactional(readOnly = true)
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

  @Transactional(readOnly = true)
  public double getTotalCaloriesThisWeek(User user) {
    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);

    java.time.LocalDateTime start = startOfWeek.atStartOfDay();
    java.time.LocalDateTime end = today.plusDays(1).atStartOfDay();

    return workoutRepository.findByUserAndDateBetween(user, start, end).stream()
        .mapToDouble(workout -> workout.getCalorieBurn() != null ? workout.getCalorieBurn() : 0.0)
        .sum();
  }
}
