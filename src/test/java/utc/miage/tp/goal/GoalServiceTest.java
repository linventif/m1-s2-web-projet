package utc.miage.tp.goal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utc.miage.tp.user.PracticeLevel;
import utc.miage.tp.user.Sex;
import utc.miage.tp.user.User;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

  @Mock private GoalRepository goalRepository;

  @InjectMocks private GoalService goalService;

  private User user =
      new User(
          "Alice",
          "Marchand",
          "alice@example.com",
          60.0,
          165.0,
          Sex.FEMALE,
          LocalDate.of(2024, 3, 31),
          PracticeLevel.BEGINNER);

  @Test
  void createGoal_createsAndPersistsCopiedGoal() {
    Goal input = new Goal("10k", GoalType.DISTANCE, 10.0, 2.2, "KM", user);
    when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Goal created = goalService.createGoal(input);

    assertNotSame(input, created);
    assertEquals("10k", created.getLabel());
    assertEquals(GoalType.DISTANCE, created.getType());
    assertEquals(10.0, created.getTargetValue());
    assertEquals(2.2, created.getCurrentValue());
    assertEquals("KM", created.getUnit());
    assertEquals(user, created.getUser());
    verify(goalRepository, times(2)).save(any(Goal.class));
  }

  @Test
  void getAll_returnsRepositoryValues() {
    List<Goal> expected =
        List.of(
            new Goal("10k", GoalType.DISTANCE, 10.0, 2.2, "KM", user),
            new Goal("20k", GoalType.DISTANCE, 20.0, 2.2, "KM", user));
    when(goalRepository.findAll()).thenReturn(expected);

    List<Goal> result = goalService.getAll();

    assertEquals(expected, result);
    verify(goalRepository).findAll();
  }
}
