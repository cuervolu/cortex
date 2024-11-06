package com.cortex.backend.education.progress.api;

import com.cortex.backend.core.domain.EntityType;
import com.cortex.backend.core.domain.UserProgress;
import java.util.List;

public interface UserProgressService {

  UserProgress saveProgress(Long userId, Long entityId, EntityType entityType);

  List<UserProgress> getUserProgress(Long userId);

  List<UserProgress> getUserProgressByEntityType(Long userId, EntityType entityType);

  boolean isEntityCompleted(Long userId, Long entityId, EntityType entityType);

  int getTotalPoints(Long userId);
  int getTotalCredits(Long userId);
  int getConsecutiveDaysActive(Long userId);
  long countCompletedEntities(Long userId, EntityType entityType);
}