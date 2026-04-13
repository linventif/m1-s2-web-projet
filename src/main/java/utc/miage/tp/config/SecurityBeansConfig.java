package utc.miage.tp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityBeansConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { // NOSONAR
    http.authorizeHttpRequests(
            auth ->
                auth
                    // 1. Les pages d'accès (ouvertes à TOUS)
                    .requestMatchers(
                        "/login",
                        "/register",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/error",
                        "/api/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v2/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**")
                    .permitAll()

                    // 2. Les routes ADMIN (STRICTEMENT réservées à l'ADMIN)
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")

                    // 3. Tout le reste est accessible aux USER (et ADMIN car l'admin a tous les
                    // droits)
                    .requestMatchers("/.well-known/**")
                    .permitAll()
                    .anyRequest()
                    .hasAnyRole("USER", "ADMIN"))
        .formLogin(
            form -> form.loginPage("/login").defaultSuccessUrl("/user/dashboard", true).permitAll())
        .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll());

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
