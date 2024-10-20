package com.cortex.backend.education.achievement.internal;

import com.cortex.backend.education.achievement.api.dto.AchievementRequest;
import com.cortex.backend.education.achievement.api.dto.AchievementResponse;
import com.cortex.backend.education.achievement.api.dto.AchievementResponse.AchievementTypeResponse;
import com.cortex.backend.core.domain.Achievement;
import com.cortex.backend.core.domain.AchievementType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AchievementMapper {

  @Mapping(target = "type", source = "type")
  AchievementResponse toAchievementResponse(Achievement achievement);

  AchievementTypeResponse toAchievementTypeResponse(AchievementType achievementType);

  @Mapping(target = "type", source = "type")
  Achievement toEntity(AchievementResponse achievementResponse);

  AchievementType toAchievementTypeEntity(AchievementTypeResponse achievementTypeResponse);

  @Mapping(target = "id", ignore = true) // id is auto-generated
  @Mapping(target = "type", source = "type")
  Achievement toEntity(AchievementRequest achievementRequest);
}
