package com.cortex.backend.education.achievement.api;

import com.cortex.backend.core.domain.UserProgress;

public interface AchievementProcessorService {

  void processAchievements(Long userId, UserProgress progress);

  void checkAndAwardAchievements(Long userId);
}