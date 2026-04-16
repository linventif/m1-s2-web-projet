package web.sportflow.workout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import web.sportflow.sport.Sport;
import web.sportflow.sport.SportName;
import web.sportflow.weather.WeatherStatsDTO;
import web.sportflow.workout.statistique.MonthlyBarView;

class WorkoutDtoCoverageTest {

  @Test
  void defaultConstructor_initializesWeather() {
    WorkoutDto dto = new WorkoutDto();

    assertNotNull(dto.getWeather());
  }

  @Test
  void fullConstructor_gettersAndSetters_work() {
    Sport sport = new Sport(SportName.Course, 8.0);
    WeatherStatsDTO weather = new WeatherStatsDTO("20", "25", "15", "21", "0.2", "10", "sunny");
    LocalDateTime date = LocalDateTime.of(2026, 4, 16, 10, 0);

    WorkoutDto dto =
        new WorkoutDto(1L, "Morning Run", "Desc", sport, date, 4.5, 45.0, 8.0, "Toulouse", weather);
    dto.setId(2L);
    dto.setName("Evening Run");
    dto.setDescription("Desc 2");
    dto.setRating(5.0);
    dto.setDuration(50.0);
    dto.setDistance(10.0);
    dto.setAddress("Paris");
    dto.setDate(date.plusHours(2));

    assertEquals(2L, dto.getId());
    assertEquals("Evening Run", dto.getName());
    assertEquals("Desc 2", dto.getDescription());
    assertEquals(sport, dto.getSport());
    assertEquals(date.plusHours(2), dto.getDate());
    assertEquals(5.0, dto.getRating());
    assertEquals(50.0, dto.getDuration());
    assertEquals(10.0, dto.getDistance());
    assertEquals("Paris", dto.getAddress());
    assertEquals(weather, dto.getWeather());
  }

  @Test
  void monthlyBarView_getters_work() {
    MonthlyBarView bar = new MonthlyBarView("Avr", 42.4, 180, true);

    assertEquals("Avr", bar.getLabel());
    assertEquals(42.4, bar.getValue());
    assertEquals(180, bar.getHeight());
    assertEquals(true, bar.isCurrentMonth());
  }
}
