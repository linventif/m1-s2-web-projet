package web.sportflow.config;

import java.util.List;
import web.sportflow.sport.Sport;
import web.sportflow.workout.WorkoutExercise;

final class ReferenceWorkoutExerciseMetrics {

  private ReferenceWorkoutExerciseMetrics() {}

  static void enrich(WorkoutExercise workoutExercise, Sport sport, int index) {
    if (workoutExercise == null || sport == null || sport.getName() == null) {
      return;
    }

    String sportName = sport.getName();
    String exerciseName =
        workoutExercise.getExercise() == null || workoutExercise.getExercise().getName() == null
            ? ""
            : workoutExercise.getExercise().getName();

    if (isElevationSport(sportName)
        || exerciseName.contains("Escalade")
        || exerciseName.contains("Montee")
        || exerciseName.contains("sentier")) {
      workoutExercise.setElevationGainM(80.0 + index * 45.0);
    }

    if (isSpeedSport(sportName)
        || exerciseName.contains("Sprint")
        || exerciseName.contains("Glisse")
        || exerciseName.contains("Pilotage")) {
      workoutExercise.setMaxSpeedKmh(18.0 + index * 6.0 + sportName.length() % 12);
    }

    if (isPrecisionSport(sportName) || exerciseName.contains("Tir")) {
      int attempts = 20 + index * 5;
      int successfulAttempts = Math.max(1, attempts - 4 - index);
      workoutExercise.setAttempts(attempts);
      workoutExercise.setSuccessfulAttempts(successfulAttempts);
      workoutExercise.setAccuracyPercent(successfulAttempts * 100.0 / attempts);
      workoutExercise.setScore(successfulAttempts * 10.0);
    }

    if (isAttemptSport(sportName)
        || exerciseName.contains("Saut")
        || exerciseName.contains("Lancer")) {
      int attempts = 8 + index * 2;
      int successfulAttempts = Math.max(1, attempts - 2);
      workoutExercise.setAttempts(attempts);
      workoutExercise.setSuccessfulAttempts(successfulAttempts);
      workoutExercise.setHeightM(1.2 + index * 0.4);
      workoutExercise.setScore(successfulAttempts * 8.0);
    }

    if (isDepthSport(sportName)) {
      workoutExercise.setDepthM(12.0 + index * 5.0);
    }

    if (isLapSport(sportName)) {
      workoutExercise.setLaps(4 + index * 2);
    }

    if (isRoundSport(sportName)) {
      workoutExercise.setRounds(3 + index);
      workoutExercise.setScore(18.0 + index * 5.0);
    }

    if (isScoreSport(sportName) && workoutExercise.getScore() == null) {
      workoutExercise.setScore(70.0 + index * 8.0);
    }
  }

  private static boolean isElevationSport(String sportName) {
    return List.of(
            "Escalade", "Alpinisme", "Randonnee", "Ski", "Parkour", "Speleologie", "Cyclisme")
        .contains(sportName);
  }

  private static boolean isSpeedSport(String sportName) {
    return List.of(
            "Course",
            "Marathon",
            "Cyclisme",
            "Football",
            "Basketball",
            "Tennis",
            "Ping_Pong",
            "Squash",
            "Ski",
            "Hockey",
            "Luge",
            "Patinage",
            "Bobsleigh",
            "Skate",
            "Formule_1",
            "Motocyclisme",
            "Aviron",
            "Canoe_Kayak",
            "Surf",
            "Voile",
            "Triathlon")
        .contains(sportName);
  }

  private static boolean isPrecisionSport(String sportName) {
    return List.of("Tir_Sportif", "Tir_Arc", "Tir_Cible", "Curling").contains(sportName);
  }

  private static boolean isAttemptSport(String sportName) {
    return List.of("Lance", "Saut", "Saut_Parachute", "Base_Jump").contains(sportName);
  }

  private static boolean isDepthSport(String sportName) {
    return List.of("Plongee", "Speleologie").contains(sportName);
  }

  private static boolean isLapSport(String sportName) {
    return List.of("Formule_1", "Motocyclisme", "Cyclisme", "Patinage", "Luge", "Bobsleigh")
        .contains(sportName);
  }

  private static boolean isRoundSport(String sportName) {
    return List.of("Judo", "Taekwondo", "Karate", "Boxe", "Escrime", "Lutte").contains(sportName);
  }

  private static boolean isScoreSport(String sportName) {
    return List.of(
            "Tennis",
            "Ping_Pong",
            "Squash",
            "Football",
            "Basketball",
            "Gymnastique",
            "Yoga",
            "Repassage_Extrem",
            "Equitation",
            "Surf",
            "Voile",
            "Pentathlon")
        .contains(sportName);
  }
}
