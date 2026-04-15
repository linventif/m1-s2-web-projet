package web.sportflow.exercise;

import java.util.List;
import java.util.Optional;
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
            exercise.getName(),
            exercise.getCaloriesPerSec(),
            exercise.getWorkoutExercises(),
            exercise.getSports());

    Exercise savedExercise = exerciseRepository.save(newExercise);

    return exerciseRepository.save(savedExercise);
  }

  @Transactional(readOnly = true)
  public List<Exercise> getAll() {
    List<Exercise> exercises = exerciseRepository.findAll();
    exercises.forEach(exercise -> exercise.getSports().size());
    return exercises;
  }

  @Transactional(readOnly = true)
  public Optional<Exercise> findById(Long id) {
    return exerciseRepository.findById(id);
  }
}
