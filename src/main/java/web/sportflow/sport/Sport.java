package web.sportflow.sport;

import jakarta.persistence.*;
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
  @Enumerated(EnumType.STRING)
  private SportName name;

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

  public Sport(SportName name, Double met) {
    this.name = name;
    this.met = met;
  }

  public Long getId() {
    return id;
  }

  public SportName getName() {
    return name;
  }

  public String getDisplayName() {
    return getNameOrDefault().name();
  }

  public boolean isDistanceRelevant() {
    return switch (getNameOrDefault()) {
      case Musculation,
          Callisthenie,
          CrossFit,
          Yoga,
          Saut_Parachute,
          Base_Jump,
          Tir_Sportif,
          Tir_Arc,
          Tir_Cible,
          Formule_1,
          Motocyclisme ->
          false;
      default -> true;
    };
  }

  public boolean isStrengthRelevant() {
    return switch (getNameOrDefault()) {
      case Musculation,
          Callisthenie,
          CrossFit,
          Escalade,
          Alpinisme,
          Gymnastique,
          Judo,
          Taekwondo,
          Karate,
          Boxe,
          Escrime,
          Lutte,
          Aviron,
          Canoe_Kayak,
          Equitation,
          Speleologie ->
          true;
      default -> false;
    };
  }

  public boolean isMobilityRelevant() {
    return switch (getNameOrDefault()) {
      case Musculation,
          Callisthenie,
          CrossFit,
          Yoga,
          Gymnastique,
          Judo,
          Taekwondo,
          Karate,
          Boxe,
          Escrime,
          Lutte,
          Skate,
          Surf,
          Equitation ->
          true;
      default -> false;
    };
  }

  public Double getMET() {
    return met;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(SportName name) {
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

  private SportName getNameOrDefault() {
    return name == null ? SportName.Course : name;
  }
}
