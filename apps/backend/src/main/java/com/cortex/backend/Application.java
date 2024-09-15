package com.cortex.backend;

import com.cortex.backend.entities.user.Role;
import com.cortex.backend.repositories.RoleRepository;
import com.cortex.backend.services.CountryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public CommandLineRunner runner(RoleRepository roleRepository, CountryService countryService) {
    return args -> {
      // Initialize roles
      List<String> roleNames = List.of("USER", "ADMIN", "MODERATOR", "MENTOR");
      for (String roleName : roleNames) {
        if (roleRepository.findByName(roleName).isEmpty()) {
          roleRepository.save(Role.builder().name(roleName).build());
        }
      }

      // Initialize countries
      countryService.initializeCountries();
    };
  }
}
