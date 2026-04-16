package web.sportflow.workout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import web.sportflow.exercise.Exercise;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;

@ExtendWith(MockitoExtension.class)
class WorkoutExerciseServiceTest {

  @Mock private WorkoutExerciseRepository repository;

  @InjectMocks private WorkoutExerciseService service;

  @Test
  void createWorkout_clonesAndSavesTwice() {
    WorkoutExercise input =
        new WorkoutExercise(120.0, 1000.0, 600.0, 10, 3, 15000.0, new Workout(), new Exercise());

    when(repository.save(any(WorkoutExercise.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    WorkoutExercise saved = service.createWorkout(input);

    assertEquals(120.0, saved.getAverageBpm());
    assertEquals(1000.0, saved.getDistanceM());
    verify(repository, org.mockito.Mockito.times(2)).save(any(WorkoutExercise.class));
  }

  @Test
  void getAllAndGetAllForUser_delegateToRepositoryFindAll() {
    User user = new User("A", "B", "a@b.c", 70.0, 180.0, Sex.MALE);
    user.setRole(Role.USER);
    List<WorkoutExercise> all = List.of(new WorkoutExercise());
    when(repository.findAll()).thenReturn(all);

    assertEquals(all, service.getAll());
    assertEquals(all, service.getAllForUser(user));
  }
}
