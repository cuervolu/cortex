package com.cortex.backend.engine.api.dto;

import com.cortex.backend.core.domain.ExerciseStatus;
import java.util.List;
import lombok.Data;

@Data
public class BulkStatusUpdateRequest {
  private List<Long> exerciseIds;
  private ExerciseStatus newStatus;
  private String reviewNotes;
}