package web.sportflow.workout;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import web.sportflow.exercise.Exercise;
import web.sportflow.friendship.FriendshipService;
import web.sportflow.notification.NotificationService;
import web.sportflow.sport.Sport;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;
import web.sportflow.weather.WeatherService;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceBranchCoverageTest {

  @Mock private WorkoutRepository workoutRepository;
  @Mock private WeatherService weatherService;
  @Mock private FriendshipService friendshipService;
  @Mock private NotificationService notificationService;

  @InjectMocks private WorkoutService workoutService;

  private User user;
  private Sport sport;

  @BeforeEach
  void setUp() {
    user = new User("Alice", "Martin", "alice@demo.local", 60.0, 165.0, Sex.FEMALE);
    user.setId(1L);
    user.setRole(Role.USER);

    sport = new Sport("Course", 9.0);
    sport.setId(10L);
  }

  @Test
  void isPublishable_acceptsEachExerciseMetricVariant() {
    List<Consumer<WorkoutExercise>> metricSetters =
        List.of(
            e -> e.setDurationSec(120.0),
            e -> e.setDistanceM(500.0),
            e -> e.setSets(3),
            e -> e.setReps(12),
            e -> e.setWeightG(5.0),
            e -> e.setAverageBpm(140.0),
            e -> e.setElevationGainM(50.0),
            e -> e.setMaxSpeedKmh(18.0),
            e -> e.setScore(42.0),
            e -> e.setAttempts(8),
            e -> e.setSuccessfulAttempts(6),
            e -> e.setAccuracyPercent(75.0),
            e -> e.setHeightM(1.2),
            e -> e.setDepthM(3.4),
            e -> e.setLaps(10),
            e -> e.setRounds(4));

    for (Consumer<WorkoutExercise> setter : metricSetters) {
      Workout workout = basePublishableWorkout();
      WorkoutExercise exercise = workout.getWorkoutExercises().getFirst();
      setter.accept(exercise);
      assertTrue(workoutService.isPublishable(workout));
    }
  }

  @Test
  void isPublishable_rejectsMissingExerciseMetricOrMandatoryFields() {
    Workout noExerciseMetric = basePublishableWorkout();
    assertFalse(workoutService.isPublishable(noExerciseMetric));

    Workout missingDescription = basePublishableWorkout();
    missingDescription.setDescription(" ");
    assertFalse(workoutService.isPublishable(missingDescription));

    Workout notPublished = basePublishableWorkout();
    WorkoutExercise metricExercise = notPublished.getWorkoutExercises().getFirst();
    metricExercise.setDistanceM(1000.0);
    notPublished.setPublished(false);
    assertFalse(workoutService.isDisplayable(notPublished));
  }

  private Workout basePublishableWorkout() {
    Workout workout = new Workout();
    workout.setName("Session");
    workout.setDescription("Good workout");
    workout.setDate(LocalDateTime.now());
    workout.setAddress("Toulouse");
    workout.setDurationSec(1800.0);
    workout.setRating(4.0);
    workout.setPublished(true);
    workout.setSport(sport);
    workout.setUser(user);

    WorkoutExercise exercise = new WorkoutExercise();
    exercise.setExercise(new Exercise("Exercise", 0.1));
    workout.setWorkoutExercises(List.of(exercise));
    return workout;
  }
}
