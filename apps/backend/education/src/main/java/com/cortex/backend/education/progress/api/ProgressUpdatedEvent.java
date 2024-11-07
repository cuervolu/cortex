package com.cortex.backend.education.progress.api;

import com.cortex.backend.core.domain.EntityType;

public record ProgressUpdatedEvent(Long userId, Long entityId, EntityType entityType) {
}