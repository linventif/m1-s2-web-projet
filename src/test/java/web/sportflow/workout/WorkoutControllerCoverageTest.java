package web.sportflow.workout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import web.sportflow.badge.Badge;
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
class WorkoutControllerCoverageTest {

  @Mock private WorkoutService workoutService;
  @Mock private CommentService commentService;
  @Mock private SportService sportService;
  @Mock private ExerciseService exerciseService;

  @InjectMocks private WorkoutController controller;

  @Test
  void listWorkouts_includesDisplaysAndUnlockedBadges() {
    User owner = user(1L, Role.USER);
    Sport running = sport(10L, "Course");
    Badge unlocked = new Badge("Course - Rookie", "desc");
    owner.getBadges().add(unlocked);

    Workout workout = new Workout();
    workout.setId(100L);
    workout.setUser(owner);
    workout.setSport(running);
    workout.setWorkoutExercises(new ArrayList<>());

    when(workoutService.getAll()).thenReturn(List.of(workout));

    Model model = new ExtendedModelMap();
    String view = controller.listWorkouts(model);

    assertEquals("user-workout", view);
    assertEquals(1, ((List<?>) model.getAttribute("workoutDisplays")).size());
    Map<?, ?> unlockedMap = (Map<?, ?>) model.getAttribute("unlockedBadgesByWorkoutId");
    assertEquals(1, ((List<?>) unlockedMap.get(100L)).size());
  }

  @Test
  void toggleKudo_and_commentEndpoints_delegateAndReturnFragments() {
    User currentUser = user(2L, Role.USER);
    Workout workout = new Workout();
    workout.setId(20L);
    workout.setUser(user(3L, Role.USER));
    workout.addKudo(currentUser);

    when(workoutService.findById(20L)).thenReturn(Optional.of(workout));

    Map<String, Object> payload = controller.toggleKudo(20L, currentUser);
    assertEquals(1, payload.get("newCount"));
    assertEquals(true, payload.get("isKudoed"));

    Model model = new ExtendedModelMap();
    String commentFragment = controller.postComment(20L, "GG", currentUser, model);
    assertEquals("components/comment-section :: comment-section", commentFragment);

    String deleteFragment = controller.deleteComment(20L, 9L, currentUser, model);
    assertEquals("components/comment-section :: comment-section", deleteFragment);

    verify(commentService).addComment(20L, currentUser.getEmail(), "GG");
    verify(commentService).deleteComment(20L, 9L, currentUser);
  }

  @Test
  void formAndEditEndpoints_handleOwnerAndNonOwnerFlows() {
    User owner = user(1L, Role.USER);
    User other = user(2L, Role.USER);

    Sport running = sport(10L, "Course");
    Exercise exercise = new Exercise();
    exercise.setId(99L);
    exercise.setSports(List.of(running));

    when(sportService.findAll()).thenReturn(List.of(running));
    when(exerciseService.getAll()).thenReturn(List.of(exercise));

    Model model = new ExtendedModelMap();
    assertEquals("user-workout-form", controller.newWorkoutForm(model, owner));

    Workout workout = new Workout();
    workout.setId(5L);
    workout.setUser(owner);
    workout.setSport(running);
    workout.setWorkoutExercises(new ArrayList<>());

    when(workoutService.findById(5L)).thenReturn(Optional.of(workout));

    Model ownerModel = new ExtendedModelMap();
    assertEquals("user-workout-form", controller.editWorkoutForm(5L, ownerModel, owner));

    Model otherModel = new ExtendedModelMap();
    assertEquals("redirect:/dashboard", controller.editWorkoutForm(5L, otherModel, other));
  }

  @Test
  void saveWorkout_coversCreateEditValidationAndExerciseParsing() {
    User owner = user(1L, Role.USER);
    User other = user(2L, Role.USER);
    Sport running = sport(10L, "Course");

    Exercise exercise = new Exercise();
    exercise.setId(100L);
    when(exerciseService.findById(100L)).thenReturn(Optional.of(exercise));
    when(sportService.buildFieldProfile(any(Sport.class)))
        .thenReturn(
            Map.of(
                SportService.FIELD_DURATION,
                true,
                SportService.FIELD_DISTANCE,
                true,
                SportService.FIELD_CARDIO,
                true));

    WorkoutDto createDto =
        new WorkoutDto(
            null,
            "Morning",
            "desc",
            running,
            LocalDateTime.now(),
            4.6,
            20.0,
            null,
            "Toulouse",
            new WeatherStatsDTO("20", "21", "19", "20", "0", "10", "clearsky"));

    String createView =
        controller.saveWorkout(
            createDto,
            List.of("100"),
            List.of("3"),
            List.of("10"),
            List.of("20"),
            List.of("30"),
            List.of("1500"),
            List.of("145"),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            "publish",
            owner,
            new RedirectAttributesModelMap());
    assertEquals("redirect:/dashboard", createView);

    Workout existing = new Workout();
    existing.setId(9L);
    existing.setUser(owner);
    existing.setWorkoutExercises(new ArrayList<>());
    when(workoutService.findById(9L)).thenReturn(Optional.of(existing));

    WorkoutDto editDto =
        new WorkoutDto(
            9L,
            "Edited",
            "desc",
            running,
            LocalDateTime.now(),
            3.5,
            25.0,
            null,
            "Paris",
            new WeatherStatsDTO("18", "20", "16", "17", "0", "12", "cloudy"));

    String editView =
        controller.saveWorkout(
            editDto,
            List.of("100"),
            List.of("2"),
            List.of("8"),
            List.of("40"),
            List.of("15"),
            List.of("500"),
            List.of("135"),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            "publish",
            owner,
            new RedirectAttributesModelMap());
    assertEquals("redirect:/dashboard", editView);

    String deniedView =
        controller.saveWorkout(
            editDto,
            List.of("100"),
            List.of("2"),
            List.of("8"),
            List.of("40"),
            List.of("15"),
            List.of("500"),
            List.of("135"),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            "publish",
            other,
            new RedirectAttributesModelMap());
    assertEquals("redirect:/dashboard", deniedView);

    verify(workoutService, org.mockito.Mockito.atLeastOnce())
        .saveWorkout(any(Workout.class), any(User.class));
  }

  @Test
  void deleteWorkout_checksPermissionsAndResponseText() {
    User owner = user(1L, Role.USER);
    User admin = user(2L, Role.ADMIN);
    User stranger = user(3L, Role.USER);

    Workout workout = new Workout();
    workout.setId(44L);
    workout.setUser(owner);
    workout.setSport(sport(10L, "Cyclisme"));
    when(workoutService.findById(44L)).thenReturn(Optional.of(workout));

    assertEquals("Séance Cyclisme", controller.deleteWorkout(44L, owner));
    assertEquals("Séance Cyclisme", controller.deleteWorkout(44L, admin));
    assertEquals("redirect:/dashboard", controller.deleteWorkout(44L, stranger));

    Workout noSport = new Workout();
    noSport.setId(45L);
    noSport.setUser(owner);
    when(workoutService.findById(45L)).thenReturn(Optional.of(noSport));
    assertTrue(controller.deleteWorkout(45L, owner).contains("activité"));
  }

  private User user(Long id, Role role) {
    User user = new User("User", "Test", "u" + id + "@demo.local", 70.0, 180.0, Sex.MALE);
    user.setId(id);
    user.setRole(role);
    return user;
  }

  private Sport sport(Long id, String name) {
    Sport sport = new Sport(name, 8.0);
    sport.setId(id);
    return sport;
  }
}
