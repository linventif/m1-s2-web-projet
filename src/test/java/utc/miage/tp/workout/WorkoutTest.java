package utc.miage.tp.workout;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import utc.miage.tp.sport.Sport;
import utc.miage.tp.user.Sex;
import utc.miage.tp.user.User;

class WorkoutTest {

  @Test
  void getCalorieBurn_usesDurationAndSportCalorieRate() {
    Sport sport = new Sport("Run", 8.0);
    User user = new User("Alice", "alice@example.com", 60.0, 165.0, Sex.FEMALE);
    Workout workout = new Workout(LocalDate.of(2026, 1, 1), 5000.0, 120.0, sport, user);

    assertEquals(16.0, workout.getCalorieBurn(), 0.0001);
  }
}
