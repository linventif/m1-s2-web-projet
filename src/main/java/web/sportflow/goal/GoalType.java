package web.sportflow.goal;

public enum GoalType {
  DISTANCE,
  DUREE,
  CALORIES,
  REPETITIONS;

  public String getLabelFr() {
    return switch (this) {
      case DISTANCE -> "Distance";
      case DUREE -> "Durée";
      case CALORIES -> "Calories";
      case REPETITIONS -> "Répétitions";
    };
  }
}
