package web.sportflow.user;

public enum PracticeLevel {
  BEGINNER,
  INTERMEDIATE,
  ADVANCED;

  public String getLabelFr() {
    return switch (this) {
      case BEGINNER -> "Débutant";
      case INTERMEDIATE -> "Intermédiaire";
      case ADVANCED -> "Avancé";
    };
  }
}
