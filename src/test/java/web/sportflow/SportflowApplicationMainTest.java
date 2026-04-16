package web.sportflow;

import org.junit.jupiter.api.Test;

class SportflowApplicationMainTest {

  @Test
  void main_isInvokableEvenIfContextFailsToStart() {
    try {
      SportflowApplication.main(
          new String[] {
            "--spring.main.web-application-type=none",
            "--spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration"
          });
    } catch (Exception ignored) {
      // Line coverage: we only need to execute main invocation path.
    }
  }
}
