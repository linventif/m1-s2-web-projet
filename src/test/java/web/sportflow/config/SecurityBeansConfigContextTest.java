package web.sportflow.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

class SecurityBeansConfigContextTest {

  @Test
  void securityFilterChainBean_isCreatedFromConfiguration() {
    try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
      context.register(TestSecurityConfig.class);
      context.refresh();

      SecurityFilterChain chain = context.getBean(SecurityFilterChain.class);
      assertNotNull(chain);
    }
  }

  @Configuration
  @EnableWebSecurity
  @Import(SecurityBeansConfig.class)
  static class TestSecurityConfig {}
}
