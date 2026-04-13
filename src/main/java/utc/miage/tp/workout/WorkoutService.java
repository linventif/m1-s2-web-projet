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
            workout.getAddress(),
            workout.getDurationSec(),
            workout.getRating(),
            workout.getSport(),
            workout.getWeather(),
            workout.getExercises(),
            workout.getUser());
    if (workout.getAddress() != null
        && !workout.getAddress().isEmpty()
        && workout.getDate() != null
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
    return workoutRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<Workout> getAllForUser(User user) {
    return workoutRepository.findAll();
  }
}
