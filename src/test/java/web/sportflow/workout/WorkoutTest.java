package web.sportflow.workout;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import web.sportflow.sport.Sport;
import web.sportflow.user.PracticeLevel;
import web.sportflow.user.Sex;
import web.sportflow.user.User;

class WorkoutTest {

  @Test
  void getCalorieBurn_usesDurationAndSportCalorieRate() {
    Sport sport = new Sport("Run", 8.0);
    User user =
        new User(
            "Alice",
            "Marchand",
            "alice@example.com",
            60.0,
            165.0,
            Sex.FEMALE,
            LocalDate.of(2024, 3, 31),
            PracticeLevel.BEGINNER);
    Workout workout =
        new Workout(
            LocalDateTime.of(2026, 1, 1, 10, 0), 5000.0, 120.0, "Toulouse", 3, null, sport, user);
    assertEquals(16.0, workout.getCalorieBurn(), 0.0001);
  }
}
