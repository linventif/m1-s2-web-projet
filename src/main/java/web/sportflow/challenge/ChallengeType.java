package web.sportflow.challenge;

public enum ChallengeType {
  DISTANCE,
  DUREE,
  CALORIE,
  REPETITION,
  ENDURENCE;

  public String getLabelFr() {
    return switch (this) {
      case DISTANCE -> "Distance";
      case DUREE -> "Durée";
      case CALORIE -> "Calories";
      case REPETITION -> "Répétitions";
      case ENDURENCE -> "Endurance";
    };
  }
}
