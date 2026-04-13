package utc.miage.tp.workout;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.ColumnDefault;
import utc.miage.tp.sport.Sport;
import utc.miage.tp.user.User;
import utc.miage.tp.weather.WeatherStatsDTO;

@Entity
@Table(name = "workout")
public class Workout {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Static

  @Column(name = "date", nullable = false)
  @ColumnDefault("current_date")
  private LocalDateTime date;

  @Column(name = "duration_sec", nullable = false)
  private Double durationSec;

  @Column(name = "address", nullable = true)
  private String address;

  @Column(name = "rating")
  private Integer rating; // note de 1 à 5 par exemple

  // Link

  @Embedded private WeatherStatsDTO weather;

  @ManyToOne(optional = false)
  @JoinColumn(name = "sport_id")
  private Sport sport;

  @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<WorkoutExercise> exercises = new ArrayList<>();

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  public Workout() {}

  public Workout(Sport sport, User user) {
    this.date = LocalDateTime.now();
    this.sport = sport;
    this.user = user;
  }

  public Workout(LocalDateTime date, Sport sport, User user) {
    this.date = date;
    this.sport = sport;
    this.user = user;
  }

  public Workout(
      LocalDateTime date, String address, WeatherStatsDTO weather, Sport sport, User user) {
    this.date = date;
    this.address = address;
    this.weather = weather;
    this.sport = sport;
    this.user = user;
  }

  public Workout(LocalDateTime date, Double durationSec, Sport sport, User user) {
    this.date = date;
    this.durationSec = durationSec;
    this.sport = sport;
    this.user = user;
  }

  public Workout(LocalDateTime date, Double durationSec, Integer rating, Sport sport, User user) {
    this.date = date;
    this.durationSec = durationSec;
    this.rating = rating;
    this.sport = sport;
    this.user = user;
  }

  public Workout(
      LocalDateTime date,
      String address,
      Double durationSec,
      Integer rating,
      Sport sport,
      WeatherStatsDTO weather,
      List<WorkoutExercise> exercises,
      User user) {
    this.date = date;
    this.address = address;
    this.durationSec = durationSec;
    this.rating = rating;
    this.sport = sport;
    this.weather = weather;
    this.exercises = exercises;
    this.user = user;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public Double getDurationSec() {
    return durationSec;
  }

  public void setDurationSec(Double durationSec) {
    this.durationSec = durationSec;
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

  public Sport getSport() {
    return sport;
  }

  public void setSport(Sport sport) {
    this.sport = sport;
  }

  public List<WorkoutExercise> getExercises() {
    return exercises;
  }

  public void setExercises(List<WorkoutExercise> exercises) {
    this.exercises = exercises;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public double getCalories() {
    if (exercises != null && !exercises.isEmpty()) {
      return exercises.stream()
          .mapToDouble(
              ex -> {
                if (ex.getDurationMin() == null) return 0.0;
                return ex.getDurationMin();
              })
          .sum();
    }

    if (sport == null || sport.getMET() == null || durationSec == null) {
      return 0.0;
    }

    return durationSec * sport.getMET();
  }
}
