package web.sportflow.user;

public enum Sex {
  MALE,
  FEMALE;

  public String getLabelFr() {
    return switch (this) {
      case MALE -> "Homme";
      case FEMALE -> "Femme";
    };
  }
}
