package com.cortex.backend;

import com.cortex.backend.user.api.CountryService;
import com.cortex.backend.user.api.RoleService;
import com.cortex.backend.core.domain.Role;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.Transactional;

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
      List<String> roleNames = List.of("USER", "ADMIN", "MODERATOR", "MENTOR");
      for (String roleName : roleNames) {
        try {
          if (roleService.findByName(roleName).isEmpty()) {
            Role role = Role.builder().name(roleName).build();
            roleService.saveRole(role);
            log.info("Role created: {}", roleName);
          } else {
            log.info("Role already exists: {}", roleName);
          }
        } catch (Exception e) {
          log.error("Error creating role {}: {}", roleName, e.getMessage());
        }
      }
      log.info("Roles initialization completed");
      // Initialize countries
      countryService.initializeCountries();
    };
  }


}
