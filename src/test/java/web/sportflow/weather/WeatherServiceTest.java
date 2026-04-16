package web.sportflow.weather;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

class WeatherServiceTest {

  private RestTemplate restTemplate;
  private RestClient restClient;

  @SuppressWarnings("rawtypes")
  private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

  @SuppressWarnings("rawtypes")
  private RestClient.RequestHeadersSpec requestHeadersSpec;

  private RestClient.ResponseSpec responseSpec;

  private WeatherService weatherService;

  @BeforeEach
  void setUp() {
    restTemplate = Mockito.mock(RestTemplate.class);
    restClient = Mockito.mock(RestClient.class);
    requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
    requestHeadersSpec = Mockito.mock(RestClient.RequestHeadersSpec.class);
    responseSpec = Mockito.mock(RestClient.ResponseSpec.class);

    when(restClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(java.util.function.Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(any(String.class), any(String.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

    weatherService = new WeatherService(restTemplate, restClient);
    ReflectionTestUtils.setField(weatherService, "baseUrlWeather", "https://meteo.example/api");
  }

  @Test
  void mapWeatherCode_coversKnownAndUnknownCodes() {
    assertEquals("clearsky", weatherService.mapWeatherCode("0"));
    assertEquals("cloudy", weatherService.mapWeatherCode("1"));
    assertEquals("fog", weatherService.mapWeatherCode("45"));
    assertEquals("drizzle", weatherService.mapWeatherCode("51"));
    assertEquals("rain", weatherService.mapWeatherCode("61"));
    assertEquals("snow", weatherService.mapWeatherCode("71"));
    assertEquals("thunderstorm", weatherService.mapWeatherCode("95"));
    assertEquals("Unknown (404)", weatherService.mapWeatherCode("404"));
  }

  @Test
  void getWeatherByCoordinates_buildsExpectedUrlForDateRangeAndSingleDate() {
    WeatherDTO expected = sampleWeather("2026-04-16T10:00");
    when(restTemplate.getForObject(any(String.class), eq(WeatherDTO.class))).thenReturn(expected);

    WeatherDTO byRange =
        weatherService.getWeather(43.6, 1.44, LocalDate.of(2026, 4, 15), LocalDate.of(2026, 4, 16));
    WeatherDTO byDate = weatherService.getWeather(43.6, 1.44, LocalDate.of(2026, 4, 16));

    assertEquals(expected, byRange);
    assertEquals(expected, byDate);

    verify(restTemplate)
        .getForObject(
            "https://meteo.example/api?latitude=43.6&longitude=1.44&hourly=temperature_2m,apparent_temperature,precipitation,wind_speed_10m,weather_code&start_date=2026-04-15&end_date=2026-04-16",
            WeatherDTO.class);
    verify(restTemplate)
        .getForObject(
            "https://meteo.example/api?latitude=43.6&longitude=1.44&hourly=temperature_2m,apparent_temperature,precipitation,wind_speed_10m,weather_code&start_date=2026-04-16&end_date=2026-04-16",
            WeatherDTO.class);
  }

  @Test
  void getWeatherByAddress_returnsNullWhenCoordinatesAreMissing() {
    WeatherService spy = Mockito.spy(weatherService);
    Mockito.doReturn(null).when(spy).getCoordinates("Unknown city");

    assertNull(spy.getWeather("Unknown city", LocalDate.of(2026, 4, 16)));
  }

  @Test
  void getCoordinates_returnsFirstResultOrEmptyMap() {
    WeatherService spy = Mockito.spy(weatherService);
    CityResultDTO result = new CityResultDTO();
    result.setLatitude(48.85);
    result.setLongitude(2.35);

    Mockito.doReturn(List.of(result)).when(spy).searchAddress("Paris");
    Mockito.doReturn(List.of()).when(spy).searchAddress("Nowhere");

    Map<String, Double> coordinates = spy.getCoordinates("Paris");
    Map<String, Double> emptyCoordinates = spy.getCoordinates("Nowhere");

    assertEquals(48.85, coordinates.get("lat"));
    assertEquals(2.35, coordinates.get("lon"));
    assertTrue(emptyCoordinates.isEmpty());
  }

  @Test
  void getCityFromCoordinates_resolvesAddressPriorityThenFallback() {
    stubReverseResponse(Map.of("address", Map.of("city", "Toulouse")));
    assertEquals("Toulouse", weatherService.getCityFromCoordinates(43.6, 1.44));

    stubReverseResponse(Map.of("address", Map.of("town", "TownName")));
    assertEquals("TownName", weatherService.getCityFromCoordinates(43.6, 1.44));

    stubReverseResponse(Map.of("address", Map.of("village", "VillageName")));
    assertEquals("VillageName", weatherService.getCityFromCoordinates(43.6, 1.44));

    stubReverseResponse(Map.of("address", Map.of("municipality", "MunicipalityName")));
    assertEquals("MunicipalityName", weatherService.getCityFromCoordinates(43.6, 1.44));

    stubReverseResponse(Map.of("address", Map.of("county", "CountyName")));
    assertEquals("CountyName", weatherService.getCityFromCoordinates(43.6, 1.44));

    stubReverseResponse(Map.of("address", Map.of("state", "StateName")));
    assertEquals("StateName", weatherService.getCityFromCoordinates(43.6, 1.44));

    stubReverseResponse(Map.of("display_name", "Fallback Display"));
    assertEquals("Fallback Display", weatherService.getCityFromCoordinates(43.6, 1.44));
  }

  @Test
  void getCityFromCoordinates_handlesNullInputsAndMissingPayload() {
    assertNull(weatherService.getCityFromCoordinates(null, 1.44));
    assertNull(weatherService.getCityFromCoordinates(43.6, null));

    stubReverseResponse(null);
    assertNull(weatherService.getCityFromCoordinates(43.6, 1.44));

    stubReverseResponse(Map.of("display_name", "   "));
    assertNull(weatherService.getCityFromCoordinates(43.6, 1.44));
  }

  @Test
  void getWeatherStats_returnsComputedAveragesAndNullWhenNoMeasurements() {
    WeatherService spy = Mockito.spy(weatherService);
    Mockito.doReturn(Map.of("lat", 43.6, "lon", 1.44)).when(spy).getCoordinates("Toulouse");

    WeatherDTO weather =
        new WeatherDTO(
            43.6,
            1.44,
            0.2,
            3600,
            "Europe/Paris",
            "CEST",
            146,
            new HourlyUnits("iso", "C", "C", "mm", "km/h", "code"),
            new Hourly(
                List.of("2026-04-16T10:00", "2026-04-16T11:00"),
                List.of(20.0, 22.0),
                List.of(19.0, 21.0),
                List.of(0.4, 0.0),
                List.of(12.0, 14.0),
                List.of("61", "61")));

    Mockito.doReturn(weather)
        .when(spy)
        .getWeather(43.6, 1.44, LocalDate.of(2026, 4, 16), LocalDate.of(2026, 4, 16));

    WeatherStatsDTO stats =
        spy.getWeatherStats("Toulouse", LocalDateTime.of(2026, 4, 16, 10, 15), 90.0);

    assertNotNull(stats);
    assertEquals("21", stats.getAverageTemperature());
    assertEquals("22", stats.getMaxTemperature());
    assertEquals("20", stats.getMinTemperature());
    assertEquals("20", stats.getAverageApparentTemperature());
    assertEquals("0.20", stats.getAveragePrecipitation());
    assertEquals("13", stats.getAverageWindSpeed());
    assertEquals("rain", stats.getWeatherIndicator());

    WeatherDTO emptyWeather =
        new WeatherDTO(
            43.6,
            1.44,
            0.2,
            3600,
            "Europe/Paris",
            "CEST",
            146,
            new HourlyUnits("iso", "C", "C", "mm", "km/h", "code"),
            new Hourly(
                List.of("2026-04-16T07:00"),
                List.of(20.0),
                List.of(19.0),
                List.of(0.4),
                List.of(12.0),
                List.of("61")));

    Mockito.doReturn(emptyWeather)
        .when(spy)
        .getWeather(43.6, 1.44, LocalDate.of(2026, 4, 16), LocalDate.of(2026, 4, 16));

    assertNull(spy.getWeatherStats("Toulouse", LocalDateTime.of(2026, 4, 16, 10, 0), 60.0));

    Mockito.doReturn(null).when(spy).getCoordinates("Nowhere");
    assertNull(spy.getWeatherStats("Nowhere", LocalDateTime.of(2026, 4, 16, 10, 0), 30.0));
  }

  private void stubReverseResponse(Map<String, Object> value) {
    when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(value);
  }

  private WeatherDTO sampleWeather(String timestamp) {
    return new WeatherDTO(
        43.6,
        1.44,
        0.2,
        3600,
        "Europe/Paris",
        "CEST",
        146,
        new HourlyUnits("iso", "C", "C", "mm", "km/h", "code"),
        new Hourly(
            List.of(timestamp),
            List.of(20.0),
            List.of(19.0),
            List.of(0.4),
            List.of(12.0),
            List.of("61")));
  }
}
