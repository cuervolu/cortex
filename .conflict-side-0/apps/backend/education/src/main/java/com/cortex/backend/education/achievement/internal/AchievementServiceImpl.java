package com.cortex.backend.education.achievement.internal;

import com.cortex.backend.core.domain.Achievement;
import com.cortex.backend.core.domain.AchievementType;
import com.cortex.backend.core.domain.UserAchievement;
import com.cortex.backend.core.domain.UserAchievementKey;
import com.cortex.backend.education.achievement.api.AchievementRepository;
import com.cortex.backend.education.achievement.api.AchievementService;
import com.cortex.backend.education.achievement.api.AchievementTypeRepository;
import com.cortex.backend.education.achievement.api.dto.AchievementRequest;
import com.cortex.backend.education.achievement.api.dto.AchievementResponse;
import com.cortex.backend.education.progress.api.UserAchievementRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

  private final AchievementRepository achievementRepository;
  private final AchievementTypeRepository achievementTypeRepository;
  private final UserAchievementRepository userAchievementRepository;
  private final AchievementMapper achievementMapper;

  @Override
  @Transactional
  public AchievementResponse createAchievement(AchievementRequest achievementRequest) {
    AchievementType achievementType = AchievementType.builder()
        .name(achievementRequest.type().name())
        .description(achievementRequest.type().description())
        .iconUrl(achievementRequest.type().iconUrl())
        .build();

    AchievementType savedAchievementType = achievementTypeRepository.save(achievementType);

    Achievement achievement = Achievement.builder()
        .type(savedAchievementType)
        .name(achievementRequest.name())
        .description(achievementRequest.description())
        .points(achievementRequest.points())
        .condition(achievementRequest.condition())
        .build();

    Achievement newAchievement = achievementRepository.save(achievement);
    return achievementMapper.toAchievementResponse(newAchievement);
  }


  @Override
  public AchievementResponse getAchievement(Long id) {
    return achievementRepository.findById(id)
        .map(achievementMapper::toAchievementResponse)
        .orElseThrow(() -> new RuntimeException("Achievement not found: " + id));
  }

  @Override
  public List<AchievementResponse> getAllAchievements() {
    List<Achievement> achievements = new ArrayList<>();
    achievementRepository.findAll().forEach(achievements::add);
    return achievements.stream()
        .map(achievementMapper::toAchievementResponse)
        .toList();
  }

  @Override
  @Transactional
  public UserAchievement awardAchievement(Long userId, Long achievementId) {
    AchievementResponse achievement = getAchievement(achievementId);
    UserAchievementKey key = new UserAchievementKey(userId, achievementId);
    UserAchievement userAchievement = new UserAchievement();
    userAchievement.setId(key);
    userAchievement.setObtainedDate(LocalDateTime.now());
    return userAchievementRepository.save(userAchievement);
  }

  @Override
  public List<UserAchievement> getUserAchievements(Long userId) {
    return userAchievementRepository.findByIdUserId(userId);
  }

  @Override
  public AchievementResponse getAchievementByName(String name) {
    return achievementRepository.findByName(name)
        .map(achievementMapper::toAchievementResponse)
        .orElseThrow(() -> new RuntimeException("Achievement not found: " + name));
  }
}