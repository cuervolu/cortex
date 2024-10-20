package com.cortex.backend.engine.internal.utils;

import com.cortex.backend.core.common.exception.HashGenerationException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for generating SHA-256 hashes.
 */
public class HashUtil {

  /**
   * Private constructor to prevent instantiation.
   */
  private HashUtil() {
    throw new UnsupportedOperationException("This class cannot be instantiated");
  }

  /**
   * Generates an SHA-256 hash for the given content.
   *
   * @param content the content to hash
   * @return the SHA-256 hash as a hexadecimal string
   * @throws HashGenerationException if the SHA-256 algorithm is not available
   */
  public static String generateSHA256Hash(String content) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] encodedHash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
      return bytesToHex(encodedHash);
    } catch (NoSuchAlgorithmException e) {
      throw new HashGenerationException("Error al generar el hash SHA-256", e);
    }
  }

  /**
   * Converts a byte array to a hexadecimal string.
   *
   * @param hash the byte array to convert
   * @return the hexadecimal string representation of the byte array
   */
  private static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (byte b : hash) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }
}