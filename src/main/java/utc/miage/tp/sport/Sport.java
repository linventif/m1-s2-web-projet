package utc.miage.tp.sport;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import utc.miage.tp.workout.Workout;

import java.io.Serializable;

@Entity
@Table(name = "sport")
public class Sport implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(name = "cal_per_min", nullable = false)
  private Double calPerMin;

  @OneToMany(mappedBy = "sport", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Workout> workouts = new ArrayList<>();

  public Sport() {}

  public Sport(String name, Double calPerMin) {
    this.name = name;
    this.calPerMin = calPerMin;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Double getCaloryPerMinutes() {
    return calPerMin;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setCaloryPerMinutes(Double calPerMin) {
    this.calPerMin = calPerMin;
  }

  public List<Workout> getWorkouts() {
    return workouts;
  }

  public void setWorkouts(List<Workout> workouts) {
    this.workouts = workouts;
  }
}
