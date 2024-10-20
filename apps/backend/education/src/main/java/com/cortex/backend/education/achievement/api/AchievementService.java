package com.cortex.backend.education.achievement.api;

import com.cortex.backend.core.domain.Achievement;
import com.cortex.backend.core.domain.UserAchievement;

import com.cortex.backend.education.achievement.api.dto.AchievementRequest;
import com.cortex.backend.education.achievement.api.dto.AchievementResponse;
import java.util.List;

public interface AchievementService {

  AchievementResponse createAchievement(AchievementRequest achievementRequest);

  AchievementResponse getAchievement(Long id);

  List<AchievementResponse> getAllAchievements();

  UserAchievement awardAchievement(Long userId, Long achievementId);

  List<UserAchievement> getUserAchievements(Long userId);

  AchievementResponse getAchievementByName(String name);
}