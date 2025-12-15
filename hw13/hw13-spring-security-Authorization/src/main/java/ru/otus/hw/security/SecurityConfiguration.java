package ru.otus.hw.security;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests((authorize) -> authorize
            .requestMatchers("/books", "/authors", "/genres", "/comments-book/").authenticated()
            .requestMatchers(POST, "/book/*/delete").hasRole("ADMIN")
            .requestMatchers(GET, "/book/*/edit", "/book/create").hasAnyRole("ADMIN", "MANAGER")
            .requestMatchers(POST, "/book/*/edit", "/book/create").hasAnyRole("ADMIN", "MANAGER")

            .anyRequest().permitAll())
        .formLogin(form ->
            form.defaultSuccessUrl("/books", false)
                .permitAll()
        );

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);

  }
}
