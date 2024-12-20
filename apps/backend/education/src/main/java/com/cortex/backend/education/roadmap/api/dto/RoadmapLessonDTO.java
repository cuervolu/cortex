package com.cortex.backend.education.roadmap.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoadmapLessonDTO implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
  private Long id;
  private String name;
  private String slug;
  private Integer credits;

  @JsonProperty("exercises")
  private List<RoadmapExerciseDTO> exercises;

  @JsonProperty("display_order")
  private Integer displayOrder;
}