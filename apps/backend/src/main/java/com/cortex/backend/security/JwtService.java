package com.cortex.backend.security;


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
/**
 * The JwtService class provides methods for handling JWT tokens.
 * It includes methods for extracting claims and username from a token, 
 * generating a token for a user, validating a token, and checking if a token is expired.
 * It also includes methods for building a token with additional claims and a specified expiration time,
 * extracting the expiration date from a token, extracting all claims from a token, and getting the signing key for the tokens.
 *
 * @author Ángel Cuervo
 */
@Service
public class JwtService {

  @Value("${application.security.jwt.expiration}")
  private long jwtExpiration;

  @Value("${application.security.jwt.secret-key}")
  private String secretKey;

  /**
   * This method is used to extract the username from the JWT token.
   *
   * @param token This is the JWT token from which the username is to be extracted.
   * @return String This returns the username extracted from the JWT token.
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * This method is used to extract a specific claim from the JWT token.
   *
   * @param <T> The type of the claim to be extracted.
   * @param token This is the JWT token from which the claim is to be extracted.
   * @param claimsResolver This is a function that takes the JWT claims and returns the specific
   *     claim.
   * @return T This returns the specific claim extracted from the JWT token.
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * This method is used to generate a JWT token for a user.
   *
   * @param userDetails This is the user details object from which the JWT token is to be generated.
   * @return String This returns the generated JWT token.
   */
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  /**
   * This method is used to generate a JWT token for a user with additional claims.
   *
   * @param extraClaims This is a map of additional claims to be included in the JWT token.
   * @param userDetails This is the user details object from which the JWT token is to be generated.
   * @return String This returns the generated JWT token.
   */
  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return buildToken(extraClaims, userDetails, jwtExpiration);
  }

  /**
   * This method is used to build a JWT token for a user with additional claims and a specified
   * expiration time.
   *
   * @param extraClaims This is a map of additional claims to be included in the JWT token.
   * @param userDetails This is the user details object from which the JWT token is to be generated.
   * @param expiration This is the expiration time for the JWT token in milliseconds.
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

  /**
   * This method is used to validate a JWT token.
   *
   * @param token This is the JWT token to be validated.
   * @param userDetails This is the user details object for the user to whom the JWT token was
   *     issued.
   * @return boolean This returns true if the token is valid (i.e., the token is not expired and the
   *     username in the token matches the username in the user details), and false otherwise.
   */
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
