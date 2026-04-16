package web.sportflow.weather;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WeatherControllerTest {

  @Mock private WeatherService weatherService;

  @InjectMocks private WeatherController weatherController;

  @Test
  void getWeatherStatsAtDate_parsesDateOnlyAndDateTimeInputs() {
    WeatherStatsDTO stats = new WeatherStatsDTO("20", "22", "19", "20", "0.0", "12", "clearsky");
    when(weatherService.getWeatherStats("Toulouse", LocalDate.of(2026, 4, 16).atStartOfDay(), 60.0))
        .thenReturn(stats);

    WeatherStatsDTO resultDateOnly =
        weatherController.getWeatherStatsAtDate("2026-04-16", "Toulouse", 60.0);

    assertEquals(stats, resultDateOnly);

    when(weatherService.getWeatherStats("Toulouse", LocalDateTime.of(2026, 4, 16, 10, 30), 30.0))
        .thenReturn(stats);
    WeatherStatsDTO resultDateTime =
        weatherController.getWeatherStatsAtDate("2026-04-16T10:30:00", "Toulouse", 30.0);

    assertEquals(stats, resultDateTime);
  }

  @Test
  void weatherControllerDelegatesCurrentAndDateQueries() {
    WeatherDTO dto =
        new WeatherDTO(
            43.0,
            1.0,
            0.0,
            0,
            "UTC",
            "UTC",
            0.0,
            new HourlyUnits("", "", "", "", "", ""),
            new Hourly(
                java.util.List.of(),
                java.util.List.of(),
                java.util.List.of(),
                java.util.List.of(),
                java.util.List.of(),
                java.util.List.of()));

    when(weatherService.getWeather("Paris", LocalDate.now())).thenReturn(dto);
    when(weatherService.getWeather("Paris", LocalDate.of(2026, 4, 20))).thenReturn(dto);

    assertEquals(dto, weatherController.getCurrentWeather("Paris"));
    assertEquals(dto, weatherController.getWeatherAtDate("2026-04-20", "Paris"));
  }

  @Test
  void getCityFromCoordinates_returnsEmptyStringWhenServiceReturnsNull() {
    when(weatherService.getCityFromCoordinates(43.6, 1.44)).thenReturn(null);

    Map<String, String> payload = weatherController.getCityFromCoordinates(43.6, 1.44);

    assertNotNull(payload);
    assertEquals("", payload.get("city"));

    ArgumentCaptor<Double> latCaptor = ArgumentCaptor.forClass(Double.class);
    ArgumentCaptor<Double> lonCaptor = ArgumentCaptor.forClass(Double.class);
    verify(weatherService).getCityFromCoordinates(latCaptor.capture(), lonCaptor.capture());
    assertEquals(43.6, latCaptor.getValue());
    assertEquals(1.44, lonCaptor.getValue());
  }
}
