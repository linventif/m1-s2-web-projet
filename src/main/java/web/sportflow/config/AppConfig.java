package web.sportflow.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

@Configuration
public class AppConfig implements WebMvcConfigurer {

  @Value("${app.avatar-upload-dir:upload_data/images/avatar}")
  private String avatarUploadDir;

  @Value("${app.badge-upload-dir:upload_data/images/badge}")
  private String badgeUploadDir;

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public RestClient restClient(@Value("${geo.base-url}") String baseUrl) {
    return RestClient.builder().baseUrl(baseUrl).build();
  }

  @Bean
  public SpringSecurityDialect springSecurityDialect() {
    return new SpringSecurityDialect();
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    Path avatarUploadPath = Paths.get(avatarUploadDir).toAbsolutePath().normalize();
    Path badgeUploadPath = Paths.get(badgeUploadDir).toAbsolutePath().normalize();
    registry
        .addResourceHandler("/avatar_upload/**")
        .addResourceLocations(avatarUploadPath.toUri().toString());
    registry
        .addResourceHandler("/badge_upload/**")
        .addResourceLocations(badgeUploadPath.toUri().toString());
  }
}
