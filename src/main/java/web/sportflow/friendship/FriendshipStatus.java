package web.sportflow.friendship;

public enum FriendshipStatus {
  PENDING,
  ACCEPTED,
  REFUSED;

  public String getLabelFr() {
    return switch (this) {
      case PENDING -> "En attente";
      case ACCEPTED -> "Acceptée";
      case REFUSED -> "Refusée";
    };
  }
}
