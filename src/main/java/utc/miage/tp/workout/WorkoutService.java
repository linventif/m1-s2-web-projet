package utc.miage.tp.workout;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utc.miage.tp.user.User;

@Service
public class WorkoutService {

  private final WorkoutRepository workoutRepository;

  public WorkoutService(WorkoutRepository workoutRepository) {
    this.workoutRepository = workoutRepository;
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
