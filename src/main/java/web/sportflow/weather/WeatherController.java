package web.sportflow.weather;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "API météo")
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

  private final WeatherService weatherService;

  @Autowired
  public WeatherController(WeatherService weatherService) {
    this.weatherService = weatherService;
  }

  /**
   * Get weather statistics for a specific date and duration.
   *
   * @param date The date and time to get the weather stats for (format: YYYY-MM-DDTHH).
   * @param address The address to get the weather stats for.
   * @param duration The duration (in minutes) to get the weather stats for.
   * @return WeatherStatsDTO containing the weather statistics.
   */
  @Operation(
      summary = "Recupere les statistiques meteo pour une date et une duree",
      description =
          "Retourne les statistiques meteo pour une adresse, a partir d'une date donnee et sur une duree exprimee en minutes.")
  @GetMapping("/stats")
  public WeatherStatsDTO getWeatherStatsAtDate(
      @Parameter(
              description =
                  "Date ou date-heure de debut au format ISO-8601, par exemple 2026-04-16 ou 2026-04-16T10:30:00")
          @RequestParam
          String date,
      @Parameter(description = "Adresse ou localisation pour laquelle recuperer la meteo")
          @RequestParam
          String address,
      @Parameter(description = "Duree analysee en minutes")
          @RequestParam
          Double duration) {
    LocalDateTime startDateTime;

    if (date.length() == 10) {
      startDateTime = LocalDate.parse(date).atStartOfDay();
    } else {
      startDateTime = LocalDateTime.parse(date);
    }
    return weatherService.getWeatherStats(address, startDateTime, duration);
  }

  @Operation(
      summary = "Recupere la meteo actuelle pour une adresse",
      description = "Retourne les informations meteo du jour pour l'adresse fournie.")
  @GetMapping("/current")
  public WeatherDTO getCurrentWeather(
      @Parameter(description = "Adresse ou localisation ciblee") @RequestParam String address) {
    return weatherService.getWeather(address, LocalDate.now());
  }

  @Operation(
      summary = "Recupere la meteo pour une date donnee",
      description =
          "Retourne les informations meteo pour une adresse a la date specifiee au format ISO-8601.")
  @GetMapping("/at/{date}")
  public WeatherDTO getWeatherAtDate(
      @Parameter(description = "Date au format ISO-8601, par exemple 2026-04-16")
          @PathVariable
          String date,
      @Parameter(description = "Adresse ou localisation ciblee") @RequestParam String address) {
    return weatherService.getWeather(address, LocalDate.parse(date));
  }

  @Operation(
      summary = "Recupere une ville a partir de coordonnees geographiques",
      description =
          "Retourne le nom de la ville associee a une latitude et une longitude donnees.")
  @GetMapping("/reverse-city")
  public Map<String, String> getCityFromCoordinates(
      @Parameter(description = "Latitude") @RequestParam Double lat,
      @Parameter(description = "Longitude") @RequestParam Double lon) {
    String city = weatherService.getCityFromCoordinates(lat, lon);
    return Map.of("city", city == null ? "" : city);
  }
}
