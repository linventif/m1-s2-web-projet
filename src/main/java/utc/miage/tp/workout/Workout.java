package utc.miage.tp.workout;

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

  @Column(name = "date", nullable = false)
  @ColumnDefault("current_date")
  private LocalDateTime date;

  @Column(nullable = false)
  private Double distance;

  @Column(name = "duration", nullable = false)
  private Double duration; // en minutes

  @Column(name = "address", nullable = true)
  private String address;

  @Column(name = "rating")
  private Integer rating; // note de 1 à 5 par exemple

  @Embedded private WeatherStatsDTO weather;

  @ManyToOne(optional = false)
  @JoinColumn(name = "sport_id")
  private Sport sport;

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

  public Workout(
      LocalDateTime date,
      Double distance,
      Double duration,
      String address,
      Integer rating,
      Sport sport,
      User user) {
    this.date = date;
    this.distance = distance;
    this.duration = duration;
    this.address = address;
    this.rating = rating;
    this.sport = sport;
    this.user = user;
  }

  // Constructor with weather (for data initialization)
  public Workout(
      LocalDateTime date,
      Double distance,
      Double duration,
      String address,
      Integer rating,
      WeatherStatsDTO weather,
      Sport sport,
      User user) {
    this.date = date;
    this.distance = distance;
    this.duration = duration;
    this.address = address;
    this.rating = rating;
    this.weather = weather;
    this.sport = sport;
    this.user = user;
  }

  public Long getId() {
    return id;
  }

  public LocalDateTime getDate() {
    return date;
  }

  public Double getDistance() {
    return distance;
  }

  public Double getDuration() {
    return duration;
  }

  public String getAddress() {
    return address;
  }

  public Integer getRating() {
    return rating;
  }

  public WeatherStatsDTO getWeather() {
    return weather;
  }

  public Sport getSport() {
    return sport;
  }

  public User getUser() {
    return user;
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
    return this.usersWhoKudoed.stream()
      .anyMatch(u -> u.getId().equals(user.getId()));
  }

  public int getKudosCount() {
    return usersWhoKudoed.size();
  }

  public Double getCalorieBurn() {
    if (sport == null || sport.getCaloryPerMinutes() == null || duration == null) {
      return 0.0;
    }
    return (duration / 60.0) * sport.getCaloryPerMinutes();
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public void setDistance(Double distance) {
    this.distance = distance;
  }

  public void setDuration(Double duration) {
    this.duration = duration;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public void setWeather(WeatherStatsDTO weather) {
    this.weather = weather;
  }

  public void setSport(Sport sport) {
    this.sport = sport;
  }
}
