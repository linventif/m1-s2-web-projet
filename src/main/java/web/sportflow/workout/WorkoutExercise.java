package web.sportflow.workout;

import jakarta.persistence.*;
import web.sportflow.exercise.Exercise;

@Entity
@Table(name = "workout_exercise")
public class WorkoutExercise {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "sets", nullable = true)
  private Integer sets;

  @Column(name = "reps", nullable = true)
  private Integer reps;

  @Column(name = "weight_g", nullable = true)
  private Double weightG;

  @Column(name = "duration_sec", nullable = true)
  private Double durationSec;

  @Column(name = "distance_m", nullable = true)
  private Double distanceM;

  @Column(name = "average_bps", nullable = true)
  private Double averageBps;

  @ManyToOne(optional = false)
  @JoinColumn(name = "workout_id")
  private Workout workout;

  @ManyToOne(optional = false)
  @JoinColumn(name = "exercise_id")
  private Exercise exercise;

  public WorkoutExercise(
      Double averageBps,
      Double distanceM,
      Double durationSec,
      Integer reps,
      Integer sets,
      Double weightG,
      Workout workout,
      Exercise exercise) {
    this.averageBps = averageBps;
    this.distanceM = distanceM;
    this.durationSec = durationSec;
    this.reps = reps;
    this.sets = sets;
    this.weightG = weightG;
    this.workout = workout;
    this.exercise = exercise;
  }

  public WorkoutExercise(
      Double averageBps,
      Double distanceM,
      Double durationSec,
      Integer reps,
      Integer sets,
      Workout workout,
      Exercise exercise) {
    this.averageBps = averageBps;
    this.distanceM = distanceM;
    this.durationSec = durationSec;
    this.reps = reps;
    this.sets = sets;
    this.workout = workout;
    this.exercise = exercise;
  }

  public WorkoutExercise(
      Double averageBps, Double distanceM, Double durationSec, Workout workout, Exercise exercise) {
    this.averageBps = averageBps;
    this.distanceM = distanceM;
    this.durationSec = durationSec;
    this.workout = workout;
    this.exercise = exercise;
  }

  public WorkoutExercise(
      Double averageBps,
      Double durationSec,
      Integer reps,
      Integer sets,
      Double weightG,
      Workout workout,
      Exercise exercise) {
    this.averageBps = averageBps;
    this.durationSec = durationSec;
    this.reps = reps;
    this.sets = sets;
    this.weightG = weightG;
    this.workout = workout;
    this.exercise = exercise;
  }

  public WorkoutExercise() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getSets() {
    return sets;
  }

  public void setSets(Integer sets) {
    this.sets = sets;
  }

  public Integer getReps() {
    return reps;
  }

  public void setReps(Integer reps) {
    this.reps = reps;
  }

  public Double getWeightKg() {
    if (weightG == null) {
      return null;
    }
    return weightG / 1000;
  }

  public void setWeightG(Double weightKg) {
    this.weightG = weightKg * 1000;
  }

  public Double getDurationMin() {
    if (durationSec == null) {
      return null;
    }
    return durationSec / 60;
  }

  public void setDurationMin(Double durationMin) {
    this.durationSec = durationMin * 60;
  }

  public Double getDurationSec() {
    return durationSec;
  }

  public void setDurationSec(Double durationSec) {
    this.durationSec = durationSec;
  }

  public Double getDistanceM() {
    return distanceM;
  }

  public void setDistanceM(Double distanceM) {
    this.distanceM = distanceM;
  }

  public Double getAverageBpm() {
    if (averageBps == null) {
      return null;
    }
    return averageBps;
  }

  public void setAverageBpm(Double averageBpm) {
    this.averageBps = averageBpm;
  }

  public Workout getWorkout() {
    return workout;
  }

  public void setWorkout(Workout workout) {
    this.workout = workout;
  }

  public Exercise getExercise() {
    return exercise;
  }

  public void setExercise(Exercise exercise) {
    this.exercise = exercise;
  }
}
