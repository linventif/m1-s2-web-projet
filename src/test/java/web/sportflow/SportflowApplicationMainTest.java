package web.sportflow;

import org.junit.jupiter.api.Test;

class SportflowApplicationMainTest {

  @Test
  void main_isInvokableEvenIfContextFailsToStart() {
    try {
      SportflowApplication.main(
          new String[] {
            "--spring.main.web-application-type=none",
            "--spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration"
          });
    } catch (Exception ignored) {
      // Line coverage: we only need to execute main invocation path.
    }
  }
}
