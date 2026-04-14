package web.sportflow.user;

public enum Role {
  USER,
  ADMIN;

  public String getAuthority() {
    return "ROLE_" + name();
  }
}
