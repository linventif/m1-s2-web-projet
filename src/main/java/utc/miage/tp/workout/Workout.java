package utc.miage.tp.workout;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.ColumnDefault;
import utc.miage.tp.sport.Sport;
import utc.miage.tp.user.User;

@Entity
@Table(name = "workout")
public class Workout {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Long id;

  @Column(name = "date", nullable = false)
  @ColumnDefault("current_date")
  private LocalDateTime date;

  @Column(name = "distance", nullable = false)
  private Double distance;

  @Column(name = "duration", nullable = false)
  private Double duration; // en minutes

  @Column(name = "address", nullable = true)
  private String address;

  @ManyToOne
  @JoinColumn(name = "sport", nullable = false)
  private Sport sport;

  @ManyToOne
  @JoinColumn(name = "userX", nullable = false)
  private User user;

  public Sport getSport() {
    return sport;
  }

  public void setSport(Sport sport) {
    this.sport = sport;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Workout() {}

  public Workout(LocalDateTime date, Double distance, Double duration, Sport sport, User user) {
    this.date = date;
    this.distance = distance;
    this.duration = duration;
    this.sport = sport;
    this.user = user;
  }

  public Long getId() {
    return id;
  }

  public Double getCalorieBurn() {
    return (this.duration / 60) * this.sport.getCaloryPerMinutes();
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
}
