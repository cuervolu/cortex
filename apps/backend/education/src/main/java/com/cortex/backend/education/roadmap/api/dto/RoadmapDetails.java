package com.cortex.backend.education.roadmap.api.dto;

import com.cortex.backend.education.course.api.dto.CourseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoadmapDetails {

  private Long id;
  private String title;
  private String description;

  @JsonProperty("image_url")
  private String imageUrl;
  private String slug;

  @JsonProperty("tag_names")
  private List<String> tagNames;
  private List<CourseResponse> courses;
  
  @JsonProperty("is_published")
  private boolean isPublished;

  @JsonProperty("created_at")
  private LocalDateTime createdAt;

  @JsonProperty("updated_at")
  private LocalDateTime updatedAt;

}
