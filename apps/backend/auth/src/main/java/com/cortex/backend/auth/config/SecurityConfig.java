package com.cortex.backend.auth.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.cortex.backend.auth.internal.ConditionalOAuth2SuccessHandler;
import com.cortex.backend.auth.internal.CustomOAuth2UserService;
import com.cortex.backend.auth.internal.CustomOidcUserService;
import com.cortex.backend.auth.internal.JwtServiceImpl;
import com.cortex.backend.auth.internal.OAuth2AuthenticationSuccessHandler;
import com.cortex.backend.auth.internal.PKCEAuthorizationRequestRepository;
import com.cortex.backend.auth.internal.PKCEAwareOAuth2SuccessHandler;
import com.cortex.backend.auth.internal.infrastructure.JwtFilter;
import com.cortex.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

  private final JwtFilter jwtAuthFilter;
  private final JwtServiceImpl jwtService;
  private final AuthenticationProvider authenticationProvider;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final CustomOidcUserService customOidcUserService;
  private final CorsConfigurationSource corsConfigurationSource;
  private final UserRepository userRepository;

  @Value("${application.frontend.callback-url}")
  private String defaultCallbackUrl;

  @Bean
  public PKCEAuthorizationRequestRepository pkceAuthorizationRequestRepository() {
    return new PKCEAuthorizationRequestRepository();
  }

  @Bean
  public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
    return new OAuth2AuthenticationSuccessHandler(jwtService, userRepository);
  }

  @Bean
  public PKCEAwareOAuth2SuccessHandler pkceAwareOAuth2SuccessHandler() {
    return new PKCEAwareOAuth2SuccessHandler(
        jwtService,
        userRepository,
        pkceAuthorizationRequestRepository()
    );
  }

  @Bean
  public ConditionalOAuth2SuccessHandler conditionalOAuth2SuccessHandler() {
    return new ConditionalOAuth2SuccessHandler(
        oAuth2AuthenticationSuccessHandler(),
        pkceAwareOAuth2SuccessHandler(),
        pkceAuthorizationRequestRepository()
    );
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            req ->
                req.requestMatchers(
                        "/auth/**",
                        "/login",
                        "/oauth2/**",
                        "/login/**",
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/chat/ai/**",
                        "/ws/**",
                        "/swagger-ui.html")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo
                .userService(customOAuth2UserService)
                .oidcUserService(customOidcUserService)
            )
            .successHandler(conditionalOAuth2SuccessHandler())
            .authorizationEndpoint(authorization -> authorization
                .authorizationRequestRepository(pkceAuthorizationRequestRepository())
            )
        )
        .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}