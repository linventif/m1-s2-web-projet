package web.sportflow.user;

public enum Role {
  USER,
  ADMIN;

  public String getLabelFr() {
    return switch (this) {
      case USER -> "Utilisateur";
      case ADMIN -> "Administrateur";
    };
  }

  public String getAuthority() {
    return "ROLE_" + name();
  }
}
