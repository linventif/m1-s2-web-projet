package utc.miage.tp.workout;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import utc.miage.tp.sport.Sport;
import utc.miage.tp.user.PracticeLevel;
import utc.miage.tp.user.Sex;
import utc.miage.tp.user.User;

class WorkoutTest {

  @Test
  void getCalorieBurn_withoutExerciseShouldReturnZero() {
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
        new Workout(null, LocalDateTime.of(2026, 1, 1, 10, 0), "Toulouse", null, sport, user);
    assertEquals(0.0, workout.getCalories(), 0.0001);
  }
}
