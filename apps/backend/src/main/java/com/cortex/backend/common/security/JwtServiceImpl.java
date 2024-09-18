package com.cortex.backend.common.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

  @Value("${application.security.jwt.expiration}")
  private long jwtExpiration;

  @Value("${application.security.jwt.secret-key}")
  private String secretKey;

  @Override
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  @Override
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }


  @Override
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }


  @Override
  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return buildToken(extraClaims, userDetails, jwtExpiration);
  }

  /**
   * This method is used to build a JWT token for a user with additional claims and a specified
   * expiration time.
   *
   * @param extraClaims This is a map of additional claims to be included in the JWT token.
   * @param userDetails This is the user details object from which the JWT token is to be
   *                    generated.
   * @param expiration  This is the expiration time for the JWT token in milliseconds.
   * @return String This returns the generated JWT token.
   */
  private String buildToken(
      Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
    var authorities =
        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    return Jwts.builder()
        .claims(extraClaims)
        .subject(userDetails.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .claim("authorities", authorities)
        .signWith(getSignInKey())
        .compact();
  }

  @Override
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  /**
   * This method is used to check if a JWT token is expired.
   *
   * @param token This is the JWT token to be checked.
   * @return boolean This returns true if the token is expired, and false otherwise.
   */
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  /**
   * This method is used to extract the expiration date from a JWT token.
   *
   * @param token This is the JWT token from which the expiration date is to be extracted.
   * @return Date This returns the expiration date extracted from the JWT token.
   */
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * This method is used to extract all claims from a JWT token.
   *
   * @param token This is the JWT token from which the claims are to be extracted.
   * @return Claims This returns the claims extracted from the JWT token.
   */
  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
  }

  /**
   * This method is used to get the signing key for the JWT tokens.
   *
   * @return SecretKey This returns the signing key for the JWT tokens.
   */
  private SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
