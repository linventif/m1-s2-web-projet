package utc.miage.tp.weather;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
   * @param date    The date and time to get the weather stats for (format: YYYY-MM-DDTHH).
   * @param address The address to get the weather stats for.
   * @param duration The duration (in minutes) to get the weather stats for.
   * @return WeatherStatsDTO containing the weather statistics.
   */
  @GetMapping("/stats")
  public WeatherStatsDTO getWeatherStatsAtDate(
      @RequestParam String date, @RequestParam String address, @RequestParam Double duration) {
    LocalDateTime startDateTime;
    
    if (date.length() == 10) {
        startDateTime = LocalDate.parse(date).atStartOfDay();
    } else {
        startDateTime = LocalDateTime.parse(date);
    }
    return weatherService.getWeatherStats(address, startDateTime, duration);
  }

  @GetMapping("/current")
  public WeatherDTO getCurrentWeather(@RequestParam String address) {
    return weatherService.getWeather(address, LocalDate.now());
  }

  @GetMapping("/at/{date}")
  public WeatherDTO getWeatherAtDate(@PathVariable String date, @RequestParam String address) {
    return weatherService.getWeather(address, LocalDate.parse(date));
  }
}
