package com.cortex.backend.auth.internal.infrastructure;

import static com.cortex.backend.common.BusinessErrorCodes.EXPIRED_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.cortex.backend.common.security.JwtServiceImpl;
import com.cortex.backend.handler.ExceptionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtServiceImpl jwtService;
  private final UserDetailsService userDetailsService;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    if (request.getServletPath().contains("/api/v1/auth/")) {
      filterChain.doFilter(request, response);
      return;
    }

    if (request.getMethod().equals("OPTIONS")) {
      response.setStatus(HttpServletResponse.SC_OK);
      filterChain.doFilter(request, response);
      return;
    }

    final String authHeader = request.getHeader(AUTHORIZATION);
    final String jwt;
    final String username;
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    jwt = authHeader.substring(7);
    try {
      username = jwtService.extractUsername(jwt);
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (jwtService.isTokenValid(jwt, userDetails)) {
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      }
    } catch (ExpiredJwtException e) {
      handleExpiredJwtException(response, e);
      return;
    }
    filterChain.doFilter(request, response);
  }


  private void handleExpiredJwtException(HttpServletResponse response, ExpiredJwtException e) throws IOException {
    ExceptionResponse exceptionResponse = ExceptionResponse.builder()
        .businessErrorCode(EXPIRED_TOKEN.getCode())
        .businessErrorDescription(EXPIRED_TOKEN.getDescription())
        .error(e.getMessage())
        .build();

    response.setStatus(EXPIRED_TOKEN.getHttpStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(objectMapper.writeValueAsString(exceptionResponse));
  }
}
