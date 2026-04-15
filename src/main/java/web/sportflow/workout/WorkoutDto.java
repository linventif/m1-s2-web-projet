package web.sportflow.workout;

import java.time.LocalDateTime;
import web.sportflow.sport.Sport;
import web.sportflow.weather.WeatherStatsDTO; // Import important

public class WorkoutDto {
  private Long id;
  private Sport sport;
  private LocalDateTime date;
  private Double duration;
  private Double distance;
  private String address;

  // L'objet météo pour mapper le formulaire
  private WeatherStatsDTO weather;

  public WorkoutDto() {
    this.weather =
        new WeatherStatsDTO(); // Initialisation pour éviter les NullPointerException dans le form
  }

  public WorkoutDto(
      Long id,
      Sport sport,
      LocalDateTime date,
      Double duration,
      Double distance,
      String address,
      WeatherStatsDTO weather) {
    this.id = id;
    this.sport = sport;
    this.date = date;
    this.duration = duration;
    this.distance = distance;
    this.address = address;
    this.weather = weather;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Sport getSport() {
    return sport;
  }

  public void setSport(Sport sport) {
    this.sport = sport;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public Double getDuration() {
    return duration;
  }

  public void setDuration(Double duration) {
    this.duration = duration;
  }

  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double distance) {
    this.distance = distance;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public WeatherStatsDTO getWeather() {
    return weather;
  }

  public void setWeather(WeatherStatsDTO weather) {
    this.weather = weather;
  }
}
