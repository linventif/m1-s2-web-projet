package web.sportflow.exercise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import web.sportflow.sport.Sport;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

  @Mock private ExerciseRepository exerciseRepository;

  @InjectMocks private ExerciseService exerciseService;

  @Test
  void createExercise_savesAClonedExercise() {
    Exercise input = new Exercise("Squat", 0.2, new ArrayList<>(), new ArrayList<>());
    when(exerciseRepository.save(any(Exercise.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Exercise result = exerciseService.createExercise(input);

    assertEquals("Squat", result.getName());
    verify(exerciseRepository, org.mockito.Mockito.times(2)).save(any(Exercise.class));
  }

  @Test
  void getAll_initializesSportsCollection() {
    Exercise exercise = new Exercise();
    exercise.setSports(new ArrayList<>(List.of(new Sport())));
    when(exerciseRepository.findAll()).thenReturn(List.of(exercise));

    List<Exercise> result = exerciseService.getAll();

    assertEquals(1, result.size());
    assertTrue(result.getFirst().getSports().size() > 0);
  }

  @Test
  void findById_delegatesToRepository() {
    Exercise exercise = new Exercise();
    when(exerciseRepository.findById(9L)).thenReturn(Optional.of(exercise));

    assertTrue(exerciseService.findById(9L).isPresent());
    verify(exerciseRepository).findById(9L);
  }
}
