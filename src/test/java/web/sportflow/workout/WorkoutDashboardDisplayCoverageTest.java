package web.sportflow.workout;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import web.sportflow.sport.Sport;
import web.sportflow.user.Role;
import web.sportflow.user.Sex;
import web.sportflow.user.User;

class WorkoutDashboardDisplayCoverageTest {

  @Test
  void dashboardDisplay_handlesAllSportSwitchBranches() {
    User user = new User("A", "B", "a@b.c", 70.0, 180.0, Sex.MALE);
    user.setId(1L);
    user.setRole(Role.USER);

    List<String> sportNames =
        List.of(
            "Course",
            "Cyclisme",
            "Natation",
            "Musculation",
            "Yoga",
            "Escalade",
            "Football",
            "Tennis",
            "Plongee",
            "Surf");
    for (int index = 0; index < sportNames.size(); index++) {
      Sport sport = new Sport(sportNames.get(index), 8.0);
      sport.setId((long) index + 1);

      Workout workout = new Workout();
      workout.setSport(sport);
      workout.setUser(user);
      workout.setDurationSec(1800.0);

      WorkoutExercise exercise = new WorkoutExercise();
      exercise.setDurationSec(600.0);
      exercise.setDistanceM(1000.0);
      exercise.setSets(3);
      exercise.setReps(8);
      exercise.setWeightG(20.0);
      exercise.setAverageBpm(130.0);
      workout.setWorkoutExercises(new ArrayList<>(java.util.List.of(exercise)));

      WorkoutDashboardDisplay display = new WorkoutDashboardDisplay(workout);
      assertNotNull(display.getSportNameLabel());
      assertTrue(display.getDurationSec() >= 0);
      assertTrue(display.getDurationMinutes() >= 0);
      assertTrue(display.getDistanceKm() >= 0);
      assertTrue(display.getAveragePaceMinPerKm() >= 0);
      assertTrue(display.getAverageSpeedKmh() >= 0);
      assertTrue(display.getTotalSets() >= 0);
      assertTrue(display.getTotalReps() >= 0);
      assertTrue(display.getMaxWeightKg() >= 0);
      assertTrue(display.getAverageBpm() >= 0);
      assertTrue(display.getCalories() >= 0);
      assertNotNull(display.getPrimaryMetricLabel());
      assertNotNull(display.getPrimaryMetricValue());
      assertNotNull(display.getSecondaryMetricLabel());
      assertNotNull(display.getSecondaryMetricValue());
    }
  }

  @Test
  void dashboardDisplay_handlesNullWorkout() {
    WorkoutDashboardDisplay display = new WorkoutDashboardDisplay(null);
    assertNotNull(display.getSportNameLabel());
    assertTrue(display.getDurationSec() >= 0);
    assertTrue(display.getDistanceKm() >= 0);
  }
}
