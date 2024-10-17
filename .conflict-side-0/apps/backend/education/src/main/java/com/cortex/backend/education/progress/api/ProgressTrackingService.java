package com.cortex.backend.education.progress.api;

import com.cortex.backend.core.domain.EntityType;
import com.cortex.backend.core.domain.UserProgress;

public interface ProgressTrackingService {
  UserProgress trackProgress(Long userId, Long entityId, EntityType entityType);
  void checkAndUpdateParentProgress(Long userId, Long entityId, EntityType entityType);
  boolean isEntityCompleted(Long userId, Long entityId, EntityType entityType);
}