package utc.miage.tp.workout;

import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "date", nullable = false)
  @ColumnDefault("current_date")
  private LocalDateTime date;

  @Column(name = "duration_sec", nullable = true)
  private Double durationSec;

  @Column(name = "address", nullable = true)
  private String address;

  @Column(name = "rating", nullable = true)
  private Integer rating; // note de 1 à 5 par exemple

  @Embedded private WeatherStatsDTO weather;

  @ManyToOne(optional = false)
  @JoinColumn(name = "sport_id")
  private Sport sport;

  @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<WorkoutExercise> exercises = new ArrayList<>();

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToMany
  @JoinTable(
      name = "workout_kudos",
      joinColumns = @JoinColumn(name = "workout_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  private Set<User> usersWhoKudoed = new HashSet<>();

  public Workout() {}

  public Workout(String address, WeatherStatsDTO weather, Sport sport, User user) {

    this.date = LocalDateTime.now();
    this.address = address;
    this.weather = weather;
    this.sport = sport;
    this.user = user;
  }

  public Workout(
      String name,
      LocalDateTime date,
      String address,
      WeatherStatsDTO weather,
      Sport sport,
      User user) {
    if (name == null) this.name = "Workout from : " + date;
    else this.name = name;
    this.date = date;
    this.address = address;
    this.weather = weather;
    this.sport = sport;
    this.user = user;
  }

  public Workout(
      String name,
      LocalDateTime date,
      String address,
      WeatherStatsDTO weather,
      List<WorkoutExercise> exercises,
      Sport sport,
      User user) {
    if (name == null) this.name = "Workout from : " + date;
    else this.name = name;
    this.date = date;
    this.address = address;
    this.weather = weather;
    this.exercises = exercises;
    this.sport = sport;
    this.user = user;
  }

  public Workout(
      String name,
      LocalDateTime date,
      String address,
      Double durationSec,
      Integer rating,
      Sport sport,
      WeatherStatsDTO weather,
      List<WorkoutExercise> exercises,
      User user) {
    this.name = name;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public Double getDurationMin() {
    return durationSec / 60;
  }

  public Set<User> getUsersWhoKudoed() {
    return usersWhoKudoed;
  }

  public void addKudo(User user) {
    usersWhoKudoed.add(user);
  }

  public void removeKudo(User user) {
    this.usersWhoKudoed.removeIf(u -> u.getId().equals(user.getId()));
  }

  public List<User> getOthersWhoKudoed(User currentUser) {
    if (this.usersWhoKudoed == null) return List.of();
    return this.usersWhoKudoed.stream()
        .filter(u -> !u.getId().equals(currentUser.getId()))
        .toList();
  }

  public boolean isKudoedBy(User user) {
    if (user == null || user.getId() == null) return false;
    return this.usersWhoKudoed.stream().anyMatch(u -> u.getId().equals(user.getId()));
  }

  public int getKudosCount() {
    return usersWhoKudoed.size();
  }

  public Double getCalorieBurn() {
    if (sport == null || sport.getMET() == null) {
      return 0.0;
    }
    return durationSec / 60;
  }

  public void setDurationMin(Double durationMin) {
    this.durationSec = durationMin * 60;
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

  public List<WorkoutExercise> getWorkoutExercises() {
    return exercises;
  }

  public void setWorkoutExercises(List<WorkoutExercise> exercises) {
    this.exercises = exercises;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public double getCalories() {
    if (sport == null || sport.getMET() == null || user == null || user.getWeight() == null) {
      return 0.0;
    }

    double totalCalories = 0.0;

    if (exercises != null && !exercises.isEmpty()) {
      for (WorkoutExercise ex : exercises) {
        if (ex == null
            || ex.getDurationSec() == null
            || ex.getDurationSec() <= 0
            || (user.getWeight() == 0.0 && (ex.getWeightKg() == null || ex.getWeightKg() == 0.0))) {
          continue;
        }

        totalCalories +=
            ex.getDurationSec()
                / 60.0
                * sport.getMET()
                * 3.5
                * (user.getWeight() + (ex.getWeightKg() == null ? 0.0 : ex.getWeightKg()))
                / 200.0;
      }

      return totalCalories;
    }

    if (durationSec == null || durationSec <= 0) {
      return 0.0;
    }

    double durationMin = durationSec / 60.0;
    return durationMin * sport.getMET() * 3.5 * user.getWeight() / 200.0;
  }
}
