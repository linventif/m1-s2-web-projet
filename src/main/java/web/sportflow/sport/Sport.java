package web.sportflow.sport;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import web.sportflow.exercise.Exercise;
import web.sportflow.workout.Workout;

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

  public String getDisplayName() {
    return getNameOrDefault();
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
    if (met >= 1.0 && met <= 23.0) this.met = met;
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

  private String getNameOrDefault() {
    return name == null ? "Course" : name;
  }
}
