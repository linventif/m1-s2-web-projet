package web.sportflow.workout;

import java.util.Locale;

public class WorkoutDashboardDisplay {
  private final Workout workout;
  private final String sportName;

  public WorkoutDashboardDisplay(Workout workout) {
    this.workout = workout;
    this.sportName =
        workout == null || workout.getSport() == null ? "Course" : workout.getSport().getName();
  }

  public Workout getWorkout() {
    return workout;
  }

  public String getSportName() {
    return sportName;
  }

  public String getSportNameLabel() {
    return getSportNameOrDefault();
  }

  public double getDurationSec() {
    double totalDurationSec = 0.0;

    if (workout != null
        && workout.getWorkoutExercises() != null
        && !workout.getWorkoutExercises().isEmpty()) {
      for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
        if (exercise != null && exercise.getDurationSec() != null) {
          totalDurationSec += exercise.getDurationSec();
        }
      }
    }

    if (totalDurationSec > 0) {
      return totalDurationSec;
    }

    return workout == null || workout.getDurationSec() == null ? 0.0 : workout.getDurationSec();
  }

  public double getDurationMinutes() {
    return getDurationSec() / 60.0;
  }

  public double getDistanceKm() {
    if (workout == null
        || workout.getWorkoutExercises() == null
        || workout.getWorkoutExercises().isEmpty()) {
      return 0.0;
    }

    double totalDistanceM = 0.0;
    for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
      if (exercise != null && exercise.getDistanceM() != null) {
        totalDistanceM += exercise.getDistanceM();
      }
    }
    return totalDistanceM / 1000.0;
  }

  public double getAveragePaceMinPerKm() {
    if (getDistanceKm() <= 0) {
      return 0.0;
    }
    return getDurationMinutes() / getDistanceKm();
  }

  public double getAverageSpeedKmh() {
    double durationHours = getDurationSec() / 3600.0;
    if (durationHours <= 0) {
      return 0.0;
    }
    return getDistanceKm() / durationHours;
  }

  public int getTotalSets() {
    if (workout == null
        || workout.getWorkoutExercises() == null
        || workout.getWorkoutExercises().isEmpty()) {
      return 0;
    }

    int totalSets = 0;
    for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
      if (exercise != null && exercise.getSets() != null) {
        totalSets += exercise.getSets();
      }
    }
    return totalSets;
  }

  public int getTotalReps() {
    if (workout == null
        || workout.getWorkoutExercises() == null
        || workout.getWorkoutExercises().isEmpty()) {
      return 0;
    }

    int totalReps = 0;
    for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
      if (exercise != null && exercise.getReps() != null) {
        totalReps += exercise.getReps();
      }
    }
    return totalReps;
  }

  public double getMaxWeightKg() {
    if (workout == null
        || workout.getWorkoutExercises() == null
        || workout.getWorkoutExercises().isEmpty()) {
      return 0.0;
    }

    double maxWeightKg = 0.0;
    for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
      if (exercise != null && exercise.getWeightKg() != null) {
        maxWeightKg = Math.max(maxWeightKg, exercise.getWeightKg());
      }
    }
    return maxWeightKg;
  }

  public double getAverageBpm() {
    if (workout == null
        || workout.getWorkoutExercises() == null
        || workout.getWorkoutExercises().isEmpty()) {
      return 0.0;
    }

    double totalBpm = 0.0;
    int count = 0;
    for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
      if (exercise != null && exercise.getAverageBpm() != null) {
        totalBpm += exercise.getAverageBpm();
        count++;
      }
    }
    return count == 0 ? 0.0 : totalBpm / count;
  }

  public double getCalories() {
    return workout == null ? 0.0 : workout.getCalories();
  }

  public String getPrimaryMetricLabel() {
    if (getDistanceKm() > 0) {
      return "Distance";
    }
    if (getTotalSets() > 0) {
      return "Séries";
    }
    if (getDurationSec() > 0) {
      return "Durée";
    }
    return "Calories";
  }

  public String getPrimaryMetricValue() {
    if (getDistanceKm() > 0) {
      return format("%.2f km", getDistanceKm());
    }
    if (getTotalSets() > 0) {
      return getTotalSets() + " séries";
    }
    if (getDurationSec() > 0) {
      return format("%.0f min", getDurationMinutes());
    }
    return format("%.0f kcal", workout.getCalorieBurn());
  }

  public String getSecondaryMetricLabel() {
    if (getAverageSpeedKmh() > 0) {
      return "Vitesse moyenne";
    }
    if (getMaxWeightKg() > 0) {
      return "Charge max";
    }
    if (getTotalReps() > 0) {
      return "Répétitions";
    }
    if (getAverageBpm() > 0) {
      return "Fréquence cardiaque";
    }
    return "Calories";
  }

  public String getSecondaryMetricValue() {
    if (getAverageSpeedKmh() > 0) {
      return format("%.1f km/h", getAverageSpeedKmh());
    }
    if (getMaxWeightKg() > 0) {
      return format("%.0f kg", getMaxWeightKg());
    }
    if (getTotalReps() > 0) {
      return getTotalReps() + " répétitions";
    }
    if (getAverageBpm() > 0) {
      return format("%.0f bpm", getAverageBpm());
    }
    return format("%.0f kcal", workout.getCalorieBurn());
  }

  private String format(String pattern, double value) {
    return String.format(Locale.ROOT, pattern, value);
  }

  private String getSportNameOrDefault() {
    return sportName == null ? "Course" : sportName;
  }
}
