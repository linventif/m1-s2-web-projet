package web.sportflow.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RegistrationDtoCoverageTest {

  @Test
  void registrationDto_recordFields_areAccessible() {
    RegistrationDTO dto =
        new RegistrationDTO(
            "alice@example.com", "secret", "Alice", "Martin", Sex.FEMALE, 58.0, 165.0);

    assertEquals("alice@example.com", dto.email());
    assertEquals("secret", dto.password());
    assertEquals("Alice", dto.firstname());
    assertEquals("Martin", dto.lastname());
    assertEquals(Sex.FEMALE, dto.sex());
    assertEquals(58.0, dto.weight());
    assertEquals(165.0, dto.height());
  }
}
