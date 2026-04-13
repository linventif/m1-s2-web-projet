package utc.miage.tp.exercise;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import utc.miage.tp.sport.Sport;
import utc.miage.tp.workout.WorkoutExercise;

@Entity
@Table(name = "exercise")
public class Exercise {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name;

  /**
   * Calories par minute pour cet exercice. Utile surtout pour la musculation et les exercices
   * guidés.
   */
  @Column(name = "calories_per_second")
  private Double caloriesPerSecond;

  @OneToMany(mappedBy = "exercise")
  private List<WorkoutExercise> workoutExercises = new ArrayList<>();

  @ManyToMany(mappedBy = "exercises")
  private List<Sport> sports = new ArrayList<>();

  public Exercise() {}

  public Exercise(
      String name,
      Double caloriesPerSecond,
      List<WorkoutExercise> workoutExercises,
      List<Sport> sports) {
    this.name = name;
    this.caloriesPerSecond = caloriesPerSecond;
    this.workoutExercises = workoutExercises;
    this.sports = sports;
  }

  public Double getCaloriesPerSec() {
    return caloriesPerSecond;
  }

  public void setCaloriesPerSec(Double caloriesPerSec) {
    this.caloriesPerSecond = caloriesPerSec;
  }

  public Double getCaloriesPerMin() {
    return caloriesPerSecond / 60;
  }

  public void setCaloriesPerMin(Double caloriesPerMin) {
    this.caloriesPerSecond = caloriesPerMin * 60;
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

  public List<Sport> getSports() {
    return sports;
  }

  public void setSports(List<Sport> sports) {
    this.sports = sports;
  }

  public List<WorkoutExercise> getWorkoutExercises() {
    return workoutExercises;
  }

  public void setWorkoutExercises(List<WorkoutExercise> workoutExercises) {
    this.workoutExercises = workoutExercises;
  }
}
