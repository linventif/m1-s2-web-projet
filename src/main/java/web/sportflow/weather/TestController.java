package web.sportflow.weather;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
  @GetMapping("/test-weather")
  public String testWeather(Model model) {
    WeatherStatsDTO data = new WeatherStatsDTO("22", "23", "20", "21", "0.05", "20", "rain");
    model.addAttribute("weatherData", data);
    return "test-page";
  }
}
