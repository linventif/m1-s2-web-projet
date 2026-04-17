package web.sportflow.workout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import web.sportflow.exercise.Exercise;
import web.sportflow.exercise.ExerciseService;
import web.sportflow.sport.Sport;
import web.sportflow.sport.SportService;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;
import web.sportflow.weather.WeatherStatsDTO;
import web.sportflow.workout.comment.CommentService;

@ExtendWith(MockitoExtension.class)
class WorkoutControllerBranchCoverageTest {

  @Mock private WorkoutService workoutService;
  @Mock private CommentService commentService;
  @Mock private SportService sportService;
  @Mock private ExerciseService exerciseService;

  @InjectMocks private WorkoutController controller;

  @Test
  void saveWorkout_publishWithMissingFields_returnsEditWithErrors() {
    User owner = user(1L);
    when(sportService.buildFieldProfile(any())).thenReturn(Map.of());
    when(workoutService.saveWorkout(any(Workout.class), any(User.class)))
        .thenAnswer(
            invocation -> {
              Workout w = invocation.getArgument(0);
              if (w.getId() == null) {
                w.setId(300L);
              }
              return w;
            });

    WorkoutDto dto =
        new WorkoutDto(null, "  ", null, null, null, 6.0, null, null, " ", new WeatherStatsDTO());
    RedirectAttributesModelMap flash = new RedirectAttributesModelMap();

    String view =
        controller.saveWorkout(
            dto, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, "publish", owner, flash);

    assertEquals("redirect:/workouts/300/edit", view);
    assertNotNull(flash.getFlashAttributes().get("errorMessage"));
    assertEquals(
        "Les changements ont ete gardes en brouillon.", flash.getFlashAttributes().get("message"));
  }

  @Test
  void saveWorkout_draftBranch_returnsEditWithDraftMessage() {
    User owner = user(1L);
    when(sportService.buildFieldProfile(any())).thenReturn(Map.of());
    when(workoutService.saveWorkout(any(Workout.class), any(User.class)))
        .thenAnswer(
            invocation -> {
              Workout w = invocation.getArgument(0);
              if (w.getId() == null) {
                w.setId(301L);
              }
              return w;
            });

    WorkoutDto dto = new WorkoutDto();
    dto.setName(" Draft ");
    dto.setDescription(" ");
    dto.setAddress(" ");
    dto.setWeather(new WeatherStatsDTO());

    RedirectAttributesModelMap flash = new RedirectAttributesModelMap();
    String view =
        controller.saveWorkout(
            dto, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, "draft", owner, flash);

    assertEquals("redirect:/workouts/301/edit", view);
    assertEquals("Brouillon enregistre.", flash.getFlashAttributes().get("message"));
  }

  @Test
  void saveWorkout_parsesEnabledFieldsAndSkipsInvalidExerciseRows() {
    User owner = user(1L);
    Sport sport = new Sport("CrossFit", 8.0);
    Exercise exercise = new Exercise("Burpees", 0.2);
    exercise.setId(100L);

    when(sportService.buildFieldProfile(any(Sport.class))).thenReturn(allFieldsEnabled());
    when(exerciseService.findById(100L)).thenReturn(Optional.of(exercise));
    when(workoutService.saveWorkout(any(Workout.class), any(User.class)))
        .thenAnswer(
            invocation -> {
              Workout w = invocation.getArgument(0);
              if (w.getId() == null) {
                w.setId(302L);
              }
              return w;
            });

    WorkoutDto dto =
        new WorkoutDto(
            null,
            " Workout ",
            " Description ",
            sport,
            LocalDateTime.now(),
            4.7,
            35.0,
            null,
            " Lyon ",
            new WeatherStatsDTO());

    RedirectAttributesModelMap flash = new RedirectAttributesModelMap();
    String view =
        controller.saveWorkout(
            dto,
            List.of("100", ""),
            List.of("3"),
            List.of("12"),
            List.of("20"),
            List.of("30"),
            List.of("1500"),
            List.of("145"),
            List.of("80"),
            List.of("22"),
            List.of("10"),
            List.of("9"),
            List.of("8"),
            List.of("88"),
            List.of("1.4"),
            List.of("2.5"),
            List.of("4"),
            List.of("5"),
            "publish",
            owner,
            flash);

    assertEquals("redirect:/dashboard", view);

    ArgumentCaptor<Workout> workoutCaptor = ArgumentCaptor.forClass(Workout.class);
    org.mockito.Mockito.verify(workoutService)
        .saveWorkout(workoutCaptor.capture(), any(User.class));
    Workout saved = workoutCaptor.getValue();
    assertEquals("Workout", saved.getName());
    assertEquals("Description", saved.getDescription());
    assertEquals("Lyon", saved.getAddress());
    assertEquals(4.5, saved.getRating());
    assertEquals(1, saved.getWorkoutExercises().size());
    assertTrue(saved.isPublished());

    WorkoutExercise savedExercise = saved.getWorkoutExercises().getFirst();
    assertEquals(3, savedExercise.getSets());
    assertEquals(12, savedExercise.getReps());
    assertEquals(20.0, savedExercise.getWeightKg());
    assertEquals(30.0, savedExercise.getDurationMin());
    assertEquals(1500.0, savedExercise.getDistanceM());
    assertEquals(145.0, savedExercise.getAverageBpm());
    assertEquals(80.0, savedExercise.getElevationGainM());
    assertEquals(22.0, savedExercise.getMaxSpeedKmh());
    assertEquals(10.0, savedExercise.getScore());
    assertEquals(9, savedExercise.getAttempts());
    assertEquals(8, savedExercise.getSuccessfulAttempts());
    assertEquals(88.0, savedExercise.getAccuracyPercent());
    assertEquals(1.4, savedExercise.getHeightM());
    assertEquals(2.5, savedExercise.getDepthM());
    assertEquals(4, savedExercise.getLaps());
    assertEquals(5, savedExercise.getRounds());
  }

  private Map<String, Boolean> allFieldsEnabled() {
    return Map.ofEntries(
        Map.entry(SportService.FIELD_DURATION, true),
        Map.entry(SportService.FIELD_DISTANCE, true),
        Map.entry(SportService.FIELD_REPETITIONS, true),
        Map.entry(SportService.FIELD_LOAD, true),
        Map.entry(SportService.FIELD_CARDIO, true),
        Map.entry(SportService.FIELD_ELEVATION, true),
        Map.entry(SportService.FIELD_SPEED, true),
        Map.entry(SportService.FIELD_SCORE, true),
        Map.entry(SportService.FIELD_ATTEMPTS, true),
        Map.entry(SportService.FIELD_ACCURACY, true),
        Map.entry(SportService.FIELD_HEIGHT, true),
        Map.entry(SportService.FIELD_DEPTH, true),
        Map.entry(SportService.FIELD_LAPS, true),
        Map.entry(SportService.FIELD_ROUNDS, true),
        Map.entry(SportService.FIELD_MOBILITY, true));
  }

  private User user(Long id) {
    User user = new User("User", "Test", "u" + id + "@demo.local", 70.0, 180.0, Sex.MALE);
    user.setId(id);
    user.setRole(Role.USER);
    return user;
  }
}
