package utc.miage.tp.weather;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

  private final RestTemplate restTemplate;

  @Autowired
  public WeatherService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Value("${weather.base-url}")
  private String baseUrlWeather;

  @Value("${geo.base-url}")
  private String baseUrlGeo;

  public String mapWeatherCode(String code) {
    return switch (code) {
      case "0" -> "clearsky";
      case "1", "2", "3" -> "cloudy";
      case "45", "48" -> "fog";
      case "51", "53", "55" -> "drizzle";
      case "61", "63", "65" -> "rain";
      case "71", "73", "75" -> "snow";
      case "95", "96", "99" -> "thunderstorm";
      default -> "Unknown (" + code + ")";
    };
  }

  public WeatherDTO getWeather(
      Double latitude, Double longitude, LocalDate startDate, LocalDate endDate) {
    String url =
        baseUrlWeather
            + "?latitude="
            + latitude
            + "&longitude="
            + longitude
            + "&hourly=temperature_2m,apparent_temperature,precipitation,wind_speed_10m,weather_code&start_date="
            + startDate
            + "&end_date="
            + endDate;
    return restTemplate.getForObject(url, WeatherDTO.class);
  }

  public WeatherDTO getWeather(Double latitude, Double longitude, LocalDate date) {
    String url =
        baseUrlWeather
            + "?latitude="
            + latitude
            + "&longitude="
            + longitude
            + "&hourly=temperature_2m,apparent_temperature,precipitation,wind_speed_10m,weather_code&start_date="
            + date
            + "&end_date="
            + date;
    return restTemplate.getForObject(url, WeatherDTO.class);
  }

  public WeatherDTO getWeather(String address, LocalDate date) {
    Map<String, Double> coordinates = getCoordinates(address);
    if (coordinates == null) {
      return null;
    }
    double latitude = coordinates.get("lat");
    double longitude = coordinates.get("lon");

    return getWeather(latitude, longitude, date);
  }

  private Map<String, Double> getCoordinates(String address) {
    String url =
        baseUrlGeo + "?name=" + URI.create(address).toASCIIString() + "&count=1&format=json";

    // Map directly to your DTO
    GeocodingResponseDTO response = restTemplate.getForObject(url, GeocodingResponseDTO.class);

    if (response != null && response.getResults() != null && !response.getResults().isEmpty()) {
      GeocodingResponseDTO.CityResultDTO firstMatch = response.getResults().get(0);

      return Map.of(
          "lat", firstMatch.getLatitude(),
          "lon", firstMatch.getLongitude());
    }

    return null;
  }

  public WeatherStatsDTO getWeatherStats(String address, LocalDateTime startDate, Double duration) {
    Map<String, Double> coordinates = getCoordinates(address);
    if (coordinates == null) return null;

    double latitude = coordinates.get("lat");
    double longitude = coordinates.get("lon");

    LocalDateTime endDate = startDate.plusMinutes(duration.longValue());
    WeatherDTO weather =
        getWeather(latitude, longitude, startDate.toLocalDate(), endDate.toLocalDate());

    double[] metrics = {0.0, 0.0, 0.0, 0.0, -999.0, 999.0}; // Better defaults than MIN/MAX
    int[] count = {0};
    final String[] firstWeatherCode = {null};

    // Calculate how many distinct hours we need to check
    int durationHours = (int) Math.ceil(duration / 60.0);

    for (int i = 0; i < durationHours; i++) {
      // 1. Move forward hour by hour
      // 2. Truncate to HOURS to get "2026-04-04T15:00" format
      LocalDateTime currentHour =
          startDate.plusHours(i).truncatedTo(java.time.temporal.ChronoUnit.HOURS);
      String dateTimeStr = currentHour.toString();

      weather
          .hourly()
          .getDataAtTime(dateTimeStr)
          .ifPresent(
              data -> {
                metrics[0] += data.temperature();
                metrics[1] += data.apparentTemperature();
                metrics[2] += data.precipitation();
                metrics[3] += data.windSpeed();
                metrics[4] = Math.max(metrics[4], data.temperature());
                metrics[5] = Math.min(metrics[5], data.temperature());

                if (firstWeatherCode[0] == null) {
                  firstWeatherCode[0] = data.weatherCode();
                }
                count[0]++;
              });
    }

    if (count[0] == 0) return null;

    return new WeatherStatsDTO(
        String.format("%.0f", metrics[0] / count[0]),
        String.format("%.0f", metrics[4]),
        String.format("%.0f", metrics[5]),
        String.format("%.0f", metrics[1] / count[0]),
        String.format("%.2f", metrics[2] / count[0]),
        String.format("%.0f", metrics[3] / count[0]),
        mapWeatherCode(firstWeatherCode[0]));
  }
}
