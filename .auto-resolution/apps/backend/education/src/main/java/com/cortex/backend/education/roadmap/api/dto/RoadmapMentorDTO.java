package com.cortex.backend.education.roadmap.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoadmapMentorDTO implements Serializable {
  @JsonProperty("full_name")
  String fullName;

  String username;

  @JsonProperty("avatar_url")
  String avatarUrl;
}