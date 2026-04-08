package utc.miage.tp.weather;

import java.time.LocalDate;
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
  private String baseUrl;

  public WeatherDTO getWeather(double latitude, double longitude, LocalDate date) {
    String url =
        baseUrl
            + "?latitude="
            + latitude
            + "&longitude="
            + longitude
            + "&hourly=temperature_2m,apparent_temperature,precipitation,wind_speed_10m&start_date="
            + date
            + "&end_date="
            + date;
    return restTemplate.getForObject(url, WeatherDTO.class);
  }
  
}
