package com.cortex.backend.education.achievement.api;

import com.cortex.backend.core.domain.User;
import com.cortex.backend.core.domain.UserAchievement;
import com.cortex.backend.education.achievement.api.dto.AchievementRequest;
import com.cortex.backend.education.achievement.api.dto.AchievementResponse;
import com.cortex.backend.education.achievement.api.dto.UserAchievementRequest;
import com.cortex.backend.education.achievement.internal.AchievementMapper;
import com.cortex.backend.education.achievement.internal.UserAchievementMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/achievements")
@RequiredArgsConstructor
public class AchievementController {

  private final AchievementService achievementService;
  private final AchievementMapper achievementMapper;
  private final UserAchievementMapper userAchievementMapper;

  @GetMapping
  public ResponseEntity<List<AchievementResponse>> getAllAchievements() {
    return ResponseEntity.ok(achievementService.getAllAchievements());
  }

  @GetMapping("/{id}")
  public ResponseEntity<AchievementResponse> getAchievement(@PathVariable Long id) {
    return ResponseEntity.ok(achievementService.getAchievement(id));
  }

  @GetMapping("/user")
  public ResponseEntity<List<UserAchievementRequest>> getUserAchievements(
      @AuthenticationPrincipal User user) {
    List<UserAchievement> userAchievements = achievementService.getUserAchievements(user.getId());
    List<UserAchievementRequest> userAchievementDTOs = userAchievements.stream()
        .map(userAchievementMapper::toUserAchievementRequest).toList();
    return ResponseEntity.ok(userAchievementDTOs);
  }

  @PostMapping
  public ResponseEntity<AchievementResponse> createAchievement(
      @RequestBody AchievementRequest achievementDTO) {
    return ResponseEntity.ok(achievementService.createAchievement(achievementDTO));
  }

}
