package utc.miage.tp.weather;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestWeatherController {

  @GetMapping("/test-weather")
  public String testWeather(Model model) {
    WeatherStatsDTO weatherData = new WeatherStatsDTO("20", "22", "18", "12", "5", "10", "cloudy");
    model.addAttribute("weatherData", weatherData);
    return "test-page";
  }
}
