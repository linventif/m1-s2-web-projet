package web.sportflow.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

class AppConfigTest {

  @Test
  void appConfig_createsBeansAndRegistersResourceHandlers() {
    AppConfig config = new AppConfig();
    ReflectionTestUtils.setField(config, "avatarUploadDir", "target/test-avatar-upload");
    ReflectionTestUtils.setField(config, "badgeUploadDir", "target/test-badge-upload");

    assertNotNull(config.restTemplate());
    assertNotNull(config.restClient("https://geo.example"));
    assertNotNull(config.springSecurityDialect());

    ResourceHandlerRegistry registry =
        new ResourceHandlerRegistry(new StaticApplicationContext(), new MockServletContext());
    config.addResourceHandlers(registry);
  }
}
