package web.sportflow.weather;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class WeatherRecordCoverageTest {

  @Test
  void weatherRecord_fieldsAreAccessible() {
    Weather weather = new Weather(LocalDate.of(2026, 4, 16), "Soleil");

    assertEquals(LocalDate.of(2026, 4, 16), weather.date());
    assertEquals("Soleil", weather.nom());
  }
}
