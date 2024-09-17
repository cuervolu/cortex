package com.cortex.backend;

import com.cortex.backend.user.domain.Role;
import com.cortex.backend.user.api.CountryService;
import com.cortex.backend.user.api.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableCaching
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
        if (roleService.findByName(roleName).isEmpty()) {
          roleService.saveRole(Role.builder().name(roleName).build());
        }
      }

      // Initialize countries
      countryService.initializeCountries();
    };
  }
}
