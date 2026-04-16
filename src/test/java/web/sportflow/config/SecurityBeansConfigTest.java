package web.sportflow.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class SecurityBeansConfigTest {

  @Test
  void passwordEncoder_isProvided() {
    SecurityBeansConfig config = new SecurityBeansConfig();
    PasswordEncoder encoder = config.passwordEncoder();
    assertNotNull(encoder);
  }
}
