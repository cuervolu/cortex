package com.cortex.backend;

import com.cortex.backend.user.api.CountryService;
import com.cortex.backend.user.api.RoleService;
import com.cortex.backend.core.domain.Role;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableCaching
@EnableScheduling
@Slf4j
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public CommandLineRunner runner(RoleService roleService, CountryService countryService) {
    return args -> {
      // Initialize roles
      List<String> roleNames = List.of("USER", "ADMIN", "MODERATOR", "MENTOR", "PREMIUM_USER");
      log.info("Starting role initialization...");

      for (String roleName : roleNames) {
        try {
          log.info("Checking if role exists: {}", roleName);
          Optional<Role> existingRole = roleService.findByName(roleName);

          if (existingRole.isEmpty()) {
            log.info("Role {} not found, creating new role", roleName);
            Role role = Role.builder()
                .name(roleName)
                .build();
            Role savedRole = roleService.saveRole(role);
            log.info("Role created successfully: {}", savedRole);
          } else {
            log.info("Role already exists: {}", existingRole.get());
          }
        } catch (Exception e) {
          log.error("Error creating role {}", roleName, e);  // Log complete stack trace
        }
      }
      log.info("Roles initialization completed");

      // Initialize countries
      countryService.initializeCountries();
    };
  }
}