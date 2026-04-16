package web.sportflow.workout;

import java.util.ArrayList;
import java.util.List;
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

  public double getBestSpeedKmh() {
    double maxSpeedKmh = 0.0;
    if (workout != null && workout.getWorkoutExercises() != null) {
      for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
        if (exercise != null && exercise.getMaxSpeedKmh() != null) {
          maxSpeedKmh = Math.max(maxSpeedKmh, exercise.getMaxSpeedKmh());
        }
      }
    }
    return maxSpeedKmh > 0 ? maxSpeedKmh : getAverageSpeedKmh();
  }

  public double getTotalElevationGainM() {
    double total = 0.0;
    if (workout != null && workout.getWorkoutExercises() != null) {
      for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
        if (exercise != null && exercise.getElevationGainM() != null) {
          total += exercise.getElevationGainM();
        }
      }
    }
    return total;
  }

  public double getTotalScore() {
    double total = 0.0;
    if (workout != null && workout.getWorkoutExercises() != null) {
      for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
        if (exercise != null && exercise.getScore() != null) {
          total += exercise.getScore();
        }
      }
    }
    return total;
  }

  public int getTotalAttempts() {
    int total = 0;
    if (workout != null && workout.getWorkoutExercises() != null) {
      for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
        if (exercise != null && exercise.getAttempts() != null) {
          total += exercise.getAttempts();
        }
      }
    }
    return total;
  }

  public int getTotalSuccessfulAttempts() {
    int total = 0;
    if (workout != null && workout.getWorkoutExercises() != null) {
      for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
        if (exercise != null && exercise.getSuccessfulAttempts() != null) {
          total += exercise.getSuccessfulAttempts();
        }
      }
    }
    return total;
  }

  public double getAverageAccuracyPercent() {
    if (getTotalAttempts() > 0) {
      return getTotalSuccessfulAttempts() * 100.0 / getTotalAttempts();
    }

    double total = 0.0;
    int count = 0;
    if (workout != null && workout.getWorkoutExercises() != null) {
      for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
        if (exercise != null && exercise.getAccuracyPercent() != null) {
          total += exercise.getAccuracyPercent();
          count++;
        }
      }
    }
    return count == 0 ? 0.0 : total / count;
  }

  public double getMaxHeightM() {
    double max = 0.0;
    if (workout != null && workout.getWorkoutExercises() != null) {
      for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
        if (exercise != null && exercise.getHeightM() != null) {
          max = Math.max(max, exercise.getHeightM());
        }
      }
    }
    return max;
  }

  public double getMaxDepthM() {
    double max = 0.0;
    if (workout != null && workout.getWorkoutExercises() != null) {
      for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
        if (exercise != null && exercise.getDepthM() != null) {
          max = Math.max(max, exercise.getDepthM());
        }
      }
    }
    return max;
  }

  public int getTotalLaps() {
    int total = 0;
    if (workout != null && workout.getWorkoutExercises() != null) {
      for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
        if (exercise != null && exercise.getLaps() != null) {
          total += exercise.getLaps();
        }
      }
    }
    return total;
  }

  public int getTotalRounds() {
    int total = 0;
    if (workout != null && workout.getWorkoutExercises() != null) {
      for (WorkoutExercise exercise : workout.getWorkoutExercises()) {
        if (exercise != null && exercise.getRounds() != null) {
          total += exercise.getRounds();
        }
      }
    }
    return total;
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
    return getMetricSummary(0).label();
  }

  public String getPrimaryMetricValue() {
    return getMetricSummary(0).value();
  }

  public boolean hasSecondaryMetric() {
    return getMetricSummaries().size() > 1;
  }

  public boolean isSecondaryMetric() {
    return hasSecondaryMetric();
  }

  public String getSecondaryMetricLabel() {
    return getMetricSummary(1).label();
  }

  public String getSecondaryMetricValue() {
    return getMetricSummary(1).value();
  }

  private MetricSummary getMetricSummary(int index) {
    List<MetricSummary> summaries = getMetricSummaries();
    if (index < summaries.size()) {
      return summaries.get(index);
    }
    return new MetricSummary("empty", "Séance", "Aucune donnée");
  }

  private List<MetricSummary> getMetricSummaries() {
    List<MetricSummary> summaries = new ArrayList<>();

    addMetric(
        summaries, getDistanceKm() > 0, "distance", "Distance", format("%.2f km", getDistanceKm()));
    addMetric(
        summaries,
        getAverageAccuracyPercent() > 0,
        "accuracy",
        "Précision",
        format("%.1f %%", getAverageAccuracyPercent()));
    addMetric(
        summaries,
        getAverageAccuracyPercent() <= 0 && getTotalAttempts() > 0,
        "attempts",
        "Tentatives",
        getTotalSuccessfulAttempts() + "/" + getTotalAttempts() + " réussites");
    addMetric(
        summaries,
        getTotalScore() > 0 && getTotalAttempts() == 0,
        "score",
        "Score",
        format("%.0f pts", getTotalScore()));
    addMetric(
        summaries,
        getTotalElevationGainM() > 0,
        "elevation",
        "Dénivelé",
        format("%.0f m D+", getTotalElevationGainM()));
    addMetric(
        summaries, getMaxDepthM() > 0, "depth", "Profondeur max", format("%.1f m", getMaxDepthM()));
    addMetric(
        summaries, getMaxHeightM() > 0, "height", "Hauteur max", format("%.1f m", getMaxHeightM()));
    addMetric(summaries, getTotalLaps() > 0, "laps", "Tours", getTotalLaps() + " tours");
    addMetric(summaries, getTotalRounds() > 0, "rounds", "Rounds", getTotalRounds() + " rounds");
    addMetric(
        summaries,
        getBestSpeedKmh() > 0,
        "speed",
        "Vitesse",
        format("%.1f km/h", getBestSpeedKmh()));
    addMetric(summaries, getTotalSets() > 0, "sets", "Séries", getTotalSets() + " séries");
    addMetric(
        summaries,
        getMaxWeightKg() > 0,
        "weight",
        "Charge max",
        format("%.0f kg", getMaxWeightKg()));
    addMetric(
        summaries, getTotalReps() > 0, "reps", "Répétitions", getTotalReps() + " répétitions");
    addMetric(
        summaries,
        getAverageBpm() > 0,
        "bpm",
        "Fréquence cardiaque",
        format("%.0f bpm", getAverageBpm()));
    addMetric(
        summaries,
        getDurationSec() > 0,
        "duration",
        "Durée",
        format("%.0f min", getDurationMinutes()));
    addMetric(
        summaries, getCalories() > 0, "calories", "Calories", format("%.0f kcal", getCalories()));

    if (summaries.isEmpty()) {
      summaries.add(new MetricSummary("empty", "Séance", "Aucune donnée"));
    }
    return summaries;
  }

  private void addMetric(
      List<MetricSummary> summaries, boolean available, String key, String label, String value) {
    if (available) {
      summaries.add(new MetricSummary(key, label, value));
    }
  }

  private String format(String pattern, double value) {
    return String.format(Locale.ROOT, pattern, value);
  }

  private String getSportNameOrDefault() {
    return sportName == null ? "Course" : sportName;
  }

  private record MetricSummary(String key, String label, String value) {}
}
