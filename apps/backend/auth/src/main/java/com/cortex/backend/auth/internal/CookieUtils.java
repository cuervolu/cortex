package com.cortex.backend.auth.internal;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CookieUtils {

  private CookieUtils() {
    throw new IllegalStateException("Utility class should not be instantiated");
  }

  public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      return Arrays.stream(cookies)
          .filter(cookie -> cookie.getName().equals(name))
          .findFirst();
    }
    return Optional.empty();
  }

  public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);
  }

  public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      Arrays.stream(cookies)
          .filter(cookie -> cookie.getName().equals(name))
          .forEach(cookie -> {
            cookie.setValue("");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
          });
    }
  }

  public static void serialize(String cookieName, Object obj, HttpServletResponse response, int maxAge) {
    try {
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      try (ObjectOutputStream objectStream = new ObjectOutputStream(byteStream)) {
        objectStream.writeObject(obj);
      }
      String serialized = Base64.getEncoder().encodeToString(byteStream.toByteArray());
      addCookie(response, cookieName, serialized, maxAge);
    } catch (Exception e) {
      log.error("Error serializing object to cookie", e);
    }
  }

  public static <T> T deserialize(HttpServletRequest request, String cookieName, Class<T> cls) {
    Optional<Cookie> cookie = getCookie(request, cookieName);
    if (cookie.isPresent()) {
      try {
        byte[] bytes = Base64.getDecoder().decode(cookie.get().getValue());
        try (ObjectInputStream objectStream = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
          Object obj = objectStream.readObject();
          if (cls.isInstance(obj)) {
            return cls.cast(obj);
          }
        }
      } catch (Exception e) {
        log.debug("Could not deserialize cookie {}: {}", cookieName, e.getMessage());
      }
    }
    return null;
  }
}