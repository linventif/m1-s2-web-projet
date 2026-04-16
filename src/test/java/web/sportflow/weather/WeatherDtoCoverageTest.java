package web.sportflow.weather;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class WeatherDtoCoverageTest {

  @Test
  void cityResultDto_gettersAndSetters_work() {
    CityResultDTO dto = new CityResultDTO();
    dto.setLatitude(43.6);
    dto.setLongitude(1.44);
    dto.setName("Toulouse");

    assertEquals(43.6, dto.getLatitude());
    assertEquals(1.44, dto.getLongitude());
    assertEquals("Toulouse", dto.getName());
  }

  @Test
  void weatherStatsDto_gettersAndSetters_work() {
    WeatherStatsDTO dto = new WeatherStatsDTO();
    dto.setAverageTemperature("20");
    dto.setMaxTemperature("25");
    dto.setMinTemperature("16");
    dto.setAverageApparentTemperature("21");
    dto.setAveragePrecipitation("0.1");
    dto.setAverageWindSpeed("12");
    dto.setWeatherIndicator("sunny");

    assertEquals("20", dto.getAverageTemperature());
    assertEquals("25", dto.getMaxTemperature());
    assertEquals("16", dto.getMinTemperature());
    assertEquals("21", dto.getAverageApparentTemperature());
    assertEquals("0.1", dto.getAveragePrecipitation());
    assertEquals("12", dto.getAverageWindSpeed());
    assertEquals("sunny", dto.getWeatherIndicator());
  }

  @Test
  void hourly_getDataAtTime_returnsDataPointWhenFound() {
    Hourly hourly =
        new Hourly(
            List.of("2026-04-16T10:00", "2026-04-16T11:00"),
            List.of(18.0, 19.0),
            List.of(17.0, 18.0),
            List.of(0.0, 0.1),
            List.of(10.0, 12.0),
            List.of("0", "1"));

    Optional<HourlyDataPoint> point = hourly.getDataAtTime("2026-04-16T11:00");

    assertTrue(point.isPresent());
    assertEquals("2026-04-16T11:00", point.get().time());
    assertEquals(19.0, point.get().temperature());
    assertEquals(18.0, point.get().apparentTemperature());
    assertEquals(0.1, point.get().precipitation());
    assertEquals(12.0, point.get().windSpeed());
    assertEquals("1", point.get().weatherCode());
  }

  @Test
  void hourly_getDataAtTime_returnsEmptyWhenNotFound() {
    Hourly hourly =
        new Hourly(
            List.of("2026-04-16T10:00"),
            List.of(18.0),
            List.of(17.0),
            List.of(0.0),
            List.of(10.0),
            List.of("0"));

    Optional<HourlyDataPoint> point = hourly.getDataAtTime("2026-04-16T12:00");

    assertTrue(point.isEmpty());
  }

  @Test
  void weatherDto_recordFields_areAccessible() {
    HourlyUnits units = new HourlyUnits("iso", "C", "C", "mm", "km/h", "code");
    Hourly hourly =
        new Hourly(
            List.of("2026-04-16T10:00"),
            List.of(18.0),
            List.of(17.0),
            List.of(0.0),
            List.of(10.0),
            List.of("0"));
    WeatherDTO dto =
        new WeatherDTO(1.0, 2.0, 0.5, 7200, "Europe/Paris", "CEST", 120.0, units, hourly);

    assertEquals(1.0, dto.latitude());
    assertEquals(2.0, dto.longitude());
    assertEquals("Europe/Paris", dto.timezone());
    assertEquals("CEST", dto.timezoneAbbreviation());
    assertEquals(units, dto.hourlyUnits());
    assertEquals(hourly, dto.hourly());
  }
}
