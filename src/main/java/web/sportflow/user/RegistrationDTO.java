package web.sportflow.user;

public record RegistrationDTO(
    String email,
    String password,
    String firstname,
    String lastname,
    Sex sex,
    Double weight,
    Double height) {}
