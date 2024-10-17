package com.cortex.backend.education.achievement.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AchievementRequest(
    @NotNull AchievementTypeRequest type,
    @NotBlank String name,
    String description,
    @NotNull @Positive Integer points,
    String condition
) {
  public record AchievementTypeRequest(
      @NotBlank String name,
      String description,
      @JsonProperty("icon_url")
      String iconUrl
  ) {}
}
