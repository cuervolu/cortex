package com.cortex.backend.education.achievement.internal;

import com.cortex.backend.education.achievement.api.dto.UserAchievementRequest;
import com.cortex.backend.core.domain.UserAchievement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserAchievementMapper {
  
  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "achievementId", source = "achievement.id")
  UserAchievementRequest toUserAchievementRequest(UserAchievement userAchievement);
}
