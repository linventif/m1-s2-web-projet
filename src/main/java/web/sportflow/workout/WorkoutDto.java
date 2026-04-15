package web.sportflow.workout;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import web.sportflow.sport.Sport;
import web.sportflow.weather.WeatherStatsDTO; // Import important

public class WorkoutDto {
  private Long id;
  private String name;
  private Sport sport;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime date;

  private Double duration;
  private Double distance;
  private String address;
  private Integer rating;

  // L'objet météo pour mapper le formulaire
  private WeatherStatsDTO weather;

  public WorkoutDto() {
    this.weather =
        new WeatherStatsDTO(); // Initialisation pour éviter les NullPointerException dans le form
  }

  public WorkoutDto(
      Long id,
      String name,
      Sport sport,
      LocalDateTime date,
      Double duration,
      Double distance,
      String address,
      Integer rating,
      WeatherStatsDTO weather) {
    this.id = id;
    this.name = name;
    this.sport = sport;
    this.date = date;
    this.duration = duration;
    this.distance = distance;
    this.address = address;
    this.rating = rating;
    this.weather = weather;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public WeatherStatsDTO getWeather() {
    return weather;
  }

  public void setWeather(WeatherStatsDTO weather) {
    this.weather = weather;
  }
}
