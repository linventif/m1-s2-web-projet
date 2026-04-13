package utc.miage.tp.exercise;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExerciseService {

  private final ExerciseRepository exerciseRepository;

  public ExerciseService(ExerciseRepository exerciseRepository) {
    this.exerciseRepository = exerciseRepository;
  }

  @Transactional
  public Exercise createExercise(Exercise exercise) {
    Exercise newExercise =
        new Exercise(
            exercise.getId(),
            exercise.getName(),
            exercise.getCaloriesPerSec(),
            exercise.getWorkoutExercises(),
            exercise.getSports());

    Exercise savedExercise = exerciseRepository.save(newExercise);

    return exerciseRepository.save(savedExercise);
  }

  @Transactional(readOnly = true)
  public List<Exercise> getAll() {
    return exerciseRepository.findAll();
  }
}
