package utc.miage.tp.weather;

import java.time.LocalDate;
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

  @GetMapping("/current")
  public WeatherDTO getCurrentWeather(
      @RequestParam(defaultValue = "48.8566") double latitude,
      @RequestParam(defaultValue = "2.3522") double longitude) {
    return weatherService.getWeather(latitude, longitude, LocalDate.now());
  }

  @GetMapping("/at/{date}")
  public WeatherDTO getWeatherAtDate(
      @PathVariable String date,
      @RequestParam(defaultValue = "48.8566") double latitude,
      @RequestParam(defaultValue = "2.3522") double longitude) {
    return weatherService.getWeather(latitude, longitude, LocalDate.parse(date));
  }

  @GetMapping("/at/{date}")
  public WeatherDTO getWeatherStats(
      @PathVariable String date,
      @RequestParam(defaultValue = "48.8566") double latitude,
      @RequestParam(defaultValue = "2.3522") double longitude) {
    return weatherService.getWeather(latitude, longitude, LocalDate.parse(date));
  }
}
