package com.cortex.backend.education.achievement.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

public record UserAchievementRequest(
    @NotNull @JsonProperty("user_id") Long userId,
    @NotNull @JsonProperty("achievement_id") Long achievementId,
    @NotNull @PastOrPresent @JsonProperty("obtained_date") LocalDateTime obtainedDate
) {

}