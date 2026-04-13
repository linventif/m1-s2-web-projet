package utc.miage.tp.sport;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import utc.miage.tp.exercise.Exercise;
import utc.miage.tp.workout.Workout;

@Entity
@Table(name = "sport")
public class Sport implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(name = "met", nullable = false)
  private Double met;

  @OneToMany(mappedBy = "sport", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Workout> workouts = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "sport_exercise",
      joinColumns = @JoinColumn(name = "sport_id"),
      inverseJoinColumns = @JoinColumn(name = "exercise_id"))
  private List<Exercise> exercises = new ArrayList<>();

  public Sport() {}

  public Sport(String name, Double met) {
    this.name = name;
    this.met = met;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Double getMET() {
    return met;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setMET(Double met) {
    if (met < 1.0 || met > 23.0) this.met = met;
    else throw new IllegalArgumentException("MET must be between 1.0 and 23.0");
  }

  public List<Workout> getWorkouts() {
    return workouts;
  }

  public void setWorkouts(List<Workout> workouts) {
    this.workouts = workouts;
  }

  public List<Exercise> getExercises() {
    return exercises;
  }

  public void setExercises(List<Exercise> exercises) {
    this.exercises = exercises;
  }
}
