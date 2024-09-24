package com.cortex.backend.core.common;

import com.github.slugify.Slugify;
import java.util.function.Predicate;
import org.springframework.stereotype.Component;

/**
 * Utility class for generating and managing slugs. This class provides methods to create slugs from
 * text and ensure their uniqueness.
 */
@Component
public class SlugUtils {

  private final Slugify slugify;

  /**
   * Constructs a new SlugUtils instance. Initializes the Slugify object with default settings.
   */
  public SlugUtils() {
    this.slugify = Slugify.builder().build();
  }

  /**
   * Generates a slug from the given text.
   *
   * @param text The text to convert into a slug.
   * @return A slug version of the input text.
   */
  public String generateSlug(String text) {
    return slugify.slugify(text);
  }

  /**
   * Generates a unique slug based on the given text. If the generated slug already exists (as
   * determined by the existsPredicate), a number is appended to the slug to make it unique.
   *
   * @param text            The text to base the slug on.
   * @param existsPredicate A predicate that returns true if a slug already exists.
   * @return A unique slug.
   */
  public String generateUniqueSlug(String text, Predicate<String> existsPredicate) {
    String baseSlug = generateSlug(text);
    String uniqueSlug = baseSlug;
    int count = 1;

    while (existsPredicate.test(uniqueSlug)) {
      uniqueSlug = baseSlug + "-" + count;
      count++;
    }

    return uniqueSlug;
  }
}