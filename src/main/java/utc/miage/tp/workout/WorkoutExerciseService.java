package utc.miage.tp.workout;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utc.miage.tp.user.User;

@Service
public class WorkoutExerciseService {

  private final WorkoutExerciseRepository workoutExerciseRepository;

  public WorkoutExerciseService(WorkoutExerciseRepository workoutExerciseRepository) {
    this.workoutExerciseRepository = workoutExerciseRepository;
  }

  @Transactional
  public WorkoutExercise createWorkout(WorkoutExercise workoutExercise) {
    WorkoutExercise newWorkoutExercise =
        new WorkoutExercise(
            workoutExercise.getAverageBpm(),
            workoutExercise.getDistanceM(),
            workoutExercise.getDurationMin(),
            workoutExercise.getSets(),
            workoutExercise.getReps(),
            workoutExercise.getWeightKg(),
            workoutExercise.getWorkout(),
            workoutExercise.getExercise());

    WorkoutExercise savedWorkoutExercise = workoutExerciseRepository.save(newWorkoutExercise);

    return workoutExerciseRepository.save(savedWorkoutExercise);
  }

  @Transactional(readOnly = true)
  public List<WorkoutExercise> getAll() {
    return workoutExerciseRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<WorkoutExercise> getAllForUser(User user) {
    return workoutExerciseRepository.findAll();
  }
}
