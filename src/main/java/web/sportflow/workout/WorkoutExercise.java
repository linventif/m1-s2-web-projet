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

  @Column(name = "elevation_gain_m", nullable = true)
  private Double elevationGainM;

  @Column(name = "max_speed_kmh", nullable = true)
  private Double maxSpeedKmh;

  @Column(name = "score", nullable = true)
  private Double score;

  @Column(name = "attempts", nullable = true)
  private Integer attempts;

  @Column(name = "successful_attempts", nullable = true)
  private Integer successfulAttempts;

  @Column(name = "accuracy_percent", nullable = true)
  private Double accuracyPercent;

  @Column(name = "height_m", nullable = true)
  private Double heightM;

  @Column(name = "depth_m", nullable = true)
  private Double depthM;

  @Column(name = "laps", nullable = true)
  private Integer laps;

  @Column(name = "rounds", nullable = true)
  private Integer rounds;

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

  public Double getElevationGainM() {
    return elevationGainM;
  }

  public void setElevationGainM(Double elevationGainM) {
    this.elevationGainM = elevationGainM;
  }

  public Double getMaxSpeedKmh() {
    return maxSpeedKmh;
  }

  public void setMaxSpeedKmh(Double maxSpeedKmh) {
    this.maxSpeedKmh = maxSpeedKmh;
  }

  public Double getScore() {
    return score;
  }

  public void setScore(Double score) {
    this.score = score;
  }

  public Integer getAttempts() {
    return attempts;
  }

  public void setAttempts(Integer attempts) {
    this.attempts = attempts;
  }

  public Integer getSuccessfulAttempts() {
    return successfulAttempts;
  }

  public void setSuccessfulAttempts(Integer successfulAttempts) {
    this.successfulAttempts = successfulAttempts;
  }

  public Double getAccuracyPercent() {
    if (accuracyPercent != null) {
      return accuracyPercent;
    }
    if (attempts == null || attempts == 0 || successfulAttempts == null) {
      return null;
    }
    return successfulAttempts * 100.0 / attempts;
  }

  public void setAccuracyPercent(Double accuracyPercent) {
    this.accuracyPercent = accuracyPercent;
  }

  public Double getHeightM() {
    return heightM;
  }

  public void setHeightM(Double heightM) {
    this.heightM = heightM;
  }

  public Double getDepthM() {
    return depthM;
  }

  public void setDepthM(Double depthM) {
    this.depthM = depthM;
  }

  public Integer getLaps() {
    return laps;
  }

  public void setLaps(Integer laps) {
    this.laps = laps;
  }

  public Integer getRounds() {
    return rounds;
  }

  public void setRounds(Integer rounds) {
    this.rounds = rounds;
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
