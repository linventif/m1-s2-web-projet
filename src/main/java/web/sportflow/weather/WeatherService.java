package web.sportflow.weather;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

  private final RestTemplate restTemplate;
  private final RestClient restClient;

  @Autowired
  public WeatherService(RestTemplate restTemplate, RestClient restClient) {
    this.restTemplate = restTemplate;
    this.restClient = restClient;
  }

  @Value("${weather.base-url}")
  private String baseUrlWeather;

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

  public List<CityResultDTO> searchAddress(String query) {
    return restClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/search")
                    .queryParam("q", query)
                    .queryParam("format", "json")
                    .queryParam("limit", 1)
                    .queryParam("addressdetails", 1)
                    .build())
        .header("User-Agent", "SchoolSpringProject/1.0 (leushuis.robbe@gmail.com)")
        .retrieve()
        .body(new ParameterizedTypeReference<List<CityResultDTO>>() {});
  }

  public Map<String, Double> getCoordinates(String address) {
    List<CityResultDTO> results = searchAddress(address);

    if (results != null && !results.isEmpty()) {
      CityResultDTO firstMatch = results.get(0);
      return Map.of(
          "lat", firstMatch.getLatitude(),
          "lon", firstMatch.getLongitude());
    }
    return Collections.emptyMap();
  }

  public String getCityFromCoordinates(Double latitude, Double longitude) {
    if (latitude == null || longitude == null) {
      return null;
    }

    Map<String, Object> result =
        restClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path("/reverse")
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .queryParam("format", "jsonv2")
                        .queryParam("zoom", 10)
                        .queryParam("addressdetails", 1)
                        .build())
            .header("User-Agent", "SchoolSpringProject/1.0 (leushuis.robbe@gmail.com)")
            .retrieve()
            .body(new ParameterizedTypeReference<Map<String, Object>>() {});

    if (result == null) {
      return null;
    }

    Object addressObject = result.get("address");
    if (addressObject instanceof Map<?, ?> addressMap) {
      String city = extractAddressPart(addressMap, "city");
      if (city != null) {
        return city;
      }
      city = extractAddressPart(addressMap, "town");
      if (city != null) {
        return city;
      }
      city = extractAddressPart(addressMap, "village");
      if (city != null) {
        return city;
      }
      city = extractAddressPart(addressMap, "municipality");
      if (city != null) {
        return city;
      }
      city = extractAddressPart(addressMap, "county");
      if (city != null) {
        return city;
      }
      city = extractAddressPart(addressMap, "state");
      if (city != null) {
        return city;
      }
    }

    Object displayName = result.get("display_name");
    if (displayName instanceof String cityName && !cityName.isBlank()) {
      return cityName;
    }
    return null;
  }

  private String extractAddressPart(Map<?, ?> addressMap, String key) {
    Object value = addressMap.get(key);
    if (value instanceof String text && !text.isBlank()) {
      return text;
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
