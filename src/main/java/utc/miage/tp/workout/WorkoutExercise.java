package utc.miage.tp.workout;

import jakarta.persistence.*;

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

  public WorkoutExercise(
      Long id,
      Double averageBps,
      Double distanceM,
      Double durationSec,
      Integer reps,
      Integer sets,
      Double weightG,
      Workout workout) {
    this.id = id;
    this.averageBps = averageBps;
    this.distanceM = distanceM;
    this.durationSec = durationSec;
    this.reps = reps;
    this.sets = sets;
    this.weightG = weightG;
    this.workout = workout;
  }

  public WorkoutExercise() {}

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
    return weightG / 1000;
  }

  public void setWeightG(Double weightKg) {
    this.weightG = weightKg * 1000;
  }

  public Double getDurationMin() {
    return durationSec / 60;
  }

  public void setDurationMin(Double durationMin) {
    this.durationSec = durationMin * 60;
  }

  public Double getDistanceM() {
    return distanceM;
  }

  public void setDistanceM(Double distanceM) {
    this.distanceM = distanceM;
  }

  public Double getAverageBpm() {
    return averageBps / 60;
  }

  public void setAverageBpm(Double averageBpm) {
    this.averageBps = averageBpm * 60;
  }

  public Workout getWorkout() {
    return workout;
  }

  public void setWorkout(Workout workout) {
    this.workout = workout;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
