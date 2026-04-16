package web.sportflow.workout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import web.sportflow.sport.Sport;
import web.sportflow.user.PracticeLevel;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;

class WorkoutAdditionalCoverageTest {

  @Test
  void constructors_kudos_and_defaults_coverAdditionalBranches() {
    User owner = user(1L, 70.0);
    Sport sport = new Sport("Course", 9.0);

    Workout withGeneratedName =
        new Workout(null, LocalDateTime.of(2026, 4, 16, 8, 0), "Paris", null, sport, owner);
    assertTrue(withGeneratedName.getName().startsWith("Workout from : "));

    Workout noDate = new Workout();
    ReflectionTestUtils.invokeMethod(noDate, "applyDefaults");
    assertNotNull(noDate.getDate());
    assertTrue(noDate.getName().startsWith("Workout du "));

    User friend = user(2L, 65.0);
    withGeneratedName.addKudo(owner);
    withGeneratedName.addKudo(friend);
    assertTrue(withGeneratedName.isKudoedBy(owner));
    assertEquals(1, withGeneratedName.getOthersWhoKudoed(owner).size());
    withGeneratedName.removeKudo(friend);
    assertEquals(1, withGeneratedName.getKudosCount());
    assertFalse(withGeneratedName.isKudoedBy(user(null, 60.0)));

    ReflectionTestUtils.setField(withGeneratedName, "usersWhoKudoed", null);
    assertTrue(withGeneratedName.getOthersWhoKudoed(owner).isEmpty());
  }

  @Test
  void calories_and_exercises_coverAdditionalBranches() {
    User owner = user(10L, 72.0);
    Sport sport = new Sport("Course", 10.0);

    Workout workout = new Workout();
    workout.setSport(sport);
    workout.setUser(owner);

    WorkoutExercise valid = new WorkoutExercise();
    valid.setDurationSec(900.0);
    valid.setWeightG(5000.0);

    WorkoutExercise missingDuration = new WorkoutExercise();
    missingDuration.setDurationSec(null);

    WorkoutExercise invalidDuration = new WorkoutExercise();
    invalidDuration.setDurationSec(0.0);

    workout.setWorkoutExercises(
        new ArrayList<>(Arrays.asList(valid, missingDuration, invalidDuration, null)));
    assertTrue(workout.getCalories() > 0.0);

    workout.setWorkoutExercises(null);
    workout.setDurationMin(30.0);
    assertEquals(30.0, workout.getDurationMin(), 0.0001);
    assertTrue(workout.getCalories() > 0.0);

    workout.setDurationSec(-1.0);
    assertEquals(0.0, workout.getCalories(), 0.0001);
  }

  private static User user(Long id, Double weightKg) {
    User user =
        new User(
            "Test",
            "User",
            "test.user+" + (id == null ? "x" : id) + "@mail.local",
            "password",
            weightKg,
            175.0,
            Sex.MALE,
            LocalDate.of(1990, 1, 1),
            PracticeLevel.INTERMEDIATE);
    user.setId(id);
    user.setRole(Role.USER);
    return user;
  }
}
