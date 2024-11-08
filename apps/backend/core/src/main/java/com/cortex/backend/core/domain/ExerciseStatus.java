package com.cortex.backend.core.domain;

public enum ExerciseStatus {
  DRAFT,            // Just created, not ready for use
  PENDING_REVIEW,   // Needs to be reviewed (by an admin or moderator)
  PUBLISHED,        // Ready for use
  DEPRECATED        // No longer recommended for use
}