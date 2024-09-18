package com.cortex.backend.common.security;

import org.springframework.security.core.userdetails.UserDetails;
import java.util.Map;
import java.util.function.Function;
import io.jsonwebtoken.Claims;

/**
 * The JwtService class provides methods for handling JWT tokens. It includes methods for extracting
 * claims and username from a token, generating a token for a user, validating a token, and checking
 * if a token is expired. It also includes methods for building a token with additional claims and a
 * specified expiration time, extracting the expiration date from a token, extracting all claims
 * from a token, and getting the signing key for the tokens.
 *
 * @author Ángel Cuervo
 */
public interface JwtService {

  /**
   * This method is used to extract the username from the JWT token.
   *
   * @param token This is the JWT token from which the username is to be extracted.
   * @return String This returns the username extracted from the JWT token.
   */
  String extractUsername(String token);


  /**
   * This method is used to extract a specific claim from the JWT token.
   *
   * @param <T>            The type of the claim to be extracted.
   * @param token          This is the JWT token from which the claim is to be extracted.
   * @param claimsResolver This is a function that takes the JWT claims and returns the specific
   *                       claim.
   * @return T This returns the specific claim extracted from the JWT token.
   */
  <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

  /**
   * This method is used to generate a JWT token for a user.
   *
   * @param userDetails This is the user details object from which the JWT token is to be
   *                    generated.
   * @return String This returns the generated JWT token.
   */
  String generateToken(UserDetails userDetails);

  /**
   * This method is used to generate a JWT token for a user with additional claims.
   *
   * @param extraClaims This is a map of additional claims to be included in the JWT token.
   * @param userDetails This is the user details object from which the JWT token is to be
   *                    generated.
   * @return String This returns the generated JWT token.
   */
  String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

  /**
   * This method is used to validate a JWT token.
   *
   * @param token       This is the JWT token to be validated.
   * @param userDetails This is the user details object for the user to whom the JWT token was
   *                    issued.
   * @return boolean This returns true if the token is valid (i.e., the token is not expired and the
   * username in the token matches the username in the user details), and false otherwise.
   */
  boolean isTokenValid(String token, UserDetails userDetails);
}