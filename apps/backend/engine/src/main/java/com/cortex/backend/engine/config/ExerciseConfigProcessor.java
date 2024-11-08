package com.cortex.backend.engine.config;

import com.cortex.backend.core.common.exception.InvalidConfigurationException;
import com.cortex.backend.core.domain.Exercise;
import com.cortex.backend.core.domain.ExerciseDifficulty;
import com.cortex.backend.engine.api.ExerciseRepository;
import com.cortex.backend.engine.internal.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExerciseConfigProcessor {
  private final ExerciseRepository exerciseRepository;

  private final Map<String, Set<PrerequisiteItem>> pendingPrerequisites = new ConcurrentHashMap<>();

  public void validateConfig(ExerciseConfig config, String exerciseName) {
    if (config.getTitle() == null || config.getTitle().trim().isEmpty()) {
      throw new InvalidConfigurationException("Exercise title is required");
    }

    ContentData content = config.getContent();
    if (content == null) {
      throw new InvalidConfigurationException("Content section is required");
    }

    if (content.getPoints() < 0) {
      throw new InvalidConfigurationException("Points must be non-negative");
    }

    if (content.getCreator() == null || content.getCreator().trim().isEmpty()) {
      throw new InvalidConfigurationException("Creator is required");
    }

    try {
      ExerciseDifficulty.valueOf(content.getDifficulty().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidConfigurationException(
          String.format("Invalid difficulty level for exercise %s: %s",
              exerciseName, content.getDifficulty())
      );
    }
  }

  public Set<Long> resolvePrerequisites(Prerequisites prerequisites, String exerciseSlug) {
    Set<Long> prerequisiteIds = new HashSet<>();

    if (prerequisites == null) {
      return prerequisiteIds;
    }

    Set<PrerequisiteItem> allPrerequisites = new HashSet<>();
    if (prerequisites.getRequired() != null) {
      allPrerequisites.addAll(prerequisites.getRequired());
    }
    if (prerequisites.getRecommended() != null) {
      allPrerequisites.addAll(prerequisites.getRecommended());
    }

    for (PrerequisiteItem item : allPrerequisites) {
      exerciseRepository.findBySlug(item.getSlug())
          .ifPresentOrElse(
              exercise -> prerequisiteIds.add(exercise.getId()),
              () -> {
                // Si no se encuentra, almacenar para procesamiento posterior
                pendingPrerequisites.computeIfAbsent(exerciseSlug, k -> new HashSet<>())
                    .add(item);
                log.debug("Prerequisite {} for exercise {} stored for later processing",
                    item.getSlug(), exerciseSlug);
              }
          );
    }

    return prerequisiteIds;
  }

  @Transactional
  public void processAllPendingPrerequisites() {
    log.info("Processing pending prerequisites for {} exercises", pendingPrerequisites.size());

    pendingPrerequisites.forEach((exerciseSlug, prerequisites) -> {
      Exercise exercise = exerciseRepository.findBySlug(exerciseSlug)
          .orElseThrow(() -> new EntityNotFoundException("Exercise not found: " + exerciseSlug));

      Set<Long> resolvedPrerequisites = new HashSet<>(exercise.getPrerequisiteExercises());

      for (PrerequisiteItem item : prerequisites) {
        exerciseRepository.findBySlug(item.getSlug())
            .ifPresent(prerequisite -> resolvedPrerequisites.add(prerequisite.getId()));
      }

      exercise.setPrerequisiteExercises(resolvedPrerequisites);
      exerciseRepository.save(exercise);
    });

    pendingPrerequisites.clear();
  }

  public Set<String> processAndValidateTags(TagsData tags) {
    Set<String> processedTags = new HashSet<>();
    if (tags != null) {
      if (tags.getConcepts() != null) {
        processedTags.addAll(tags.getConcepts());
      }
      if (tags.getTechniques() != null) {
        processedTags.addAll(tags.getTechniques());
      }
    }
    return processedTags;
  }
}