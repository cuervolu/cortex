package com.cortex.backend.education.achievement.internal;

import com.cortex.backend.core.domain.Achievement;
import com.cortex.backend.core.domain.EntityType;
import com.cortex.backend.core.domain.UserProgress;
import com.cortex.backend.education.achievement.api.AchievementProcessorService;
import com.cortex.backend.education.achievement.api.AchievementService;
import com.cortex.backend.education.achievement.api.dto.AchievementResponse;
import com.cortex.backend.education.course.api.CourseService;
import com.cortex.backend.education.lesson.api.LessonService;
import com.cortex.backend.education.lesson.api.dto.LessonResponse;
import com.cortex.backend.education.module.api.ModuleService;
import com.cortex.backend.education.progress.api.UserProgressService;
import com.cortex.backend.education.roadmap.api.RoadmapService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AchievementProcessorServiceImpl implements AchievementProcessorService {

  private final AchievementService achievementService;
  private final AchievementMapper achievementMapper;
  private final UserProgressService userProgressService;
  private final LessonService lessonService;
  private final ModuleService moduleService;
  private final CourseService courseService;
  private final RoadmapService roadmapService;

  @Override
  @Transactional
  public void processAchievements(Long userId, UserProgress progress) {
    switch (progress.getId().getEntityType()) {
      case LESSON:
        processLessonAchievements(userId, progress.getId().getEntityId());
        break;
      case MODULE:
        processModuleAchievements(userId, progress.getId().getEntityId());
        break;
      case COURSE:
        processCourseAchievements(userId, progress.getId().getEntityId());
        break;
      case ROADMAP:
        processRoadmapAchievements(userId, progress.getId().getEntityId());
        break;
    }
  }

  @Override
  @Transactional
  public void checkAndAwardAchievements(Long userId) {
    checkTotalPointsAchievements(userId);
    checkTotalCreditsAchievements(userId);
    checkConsecutiveDaysAchievements(userId);
    // Añadir más verificaciones globales aquí
  }

  private void processLessonAchievements(Long userId, Long lessonId) {
    Optional<LessonResponse> lesson = lessonService.getLessonById(lessonId);
    if (lesson.isPresent()) {
      awardAchievementIfNotExists(userId, "COMPLETE_FIRST_LESSON");
      checkAllLessonsInModuleCompleted(userId, lesson.get().getModuleId());
    }
  }

  private void processModuleAchievements(Long userId, Long moduleId) {
    awardAchievementIfNotExists(userId, "COMPLETE_FIRST_MODULE");
    checkAllModulesInCourseCompleted(userId, moduleService.getCourseIdForModule(moduleId));
  }

  private void processCourseAchievements(Long userId, Long courseId) {
    awardAchievementIfNotExists(userId, "COMPLETE_FIRST_COURSE");
    checkMultipleCoursesCompleted(userId);
  }

  private void processRoadmapAchievements(Long userId, Long roadmapId) {
    awardAchievementIfNotExists(userId, "COMPLETE_FIRST_ROADMAP");
    checkMultipleRoadmapsCompleted(userId);
  }

  private void checkAllLessonsInModuleCompleted(Long userId, Long moduleId) {
    if (moduleService.areAllLessonsCompleted(userId, moduleId)) {
      awardAchievementIfNotExists(userId, "COMPLETE_ALL_LESSONS_IN_MODULE");
    }
  }

  private void checkAllModulesInCourseCompleted(Long userId, Long courseId) {
    if (courseService.areAllModulesCompleted(userId, courseId)) {
      awardAchievementIfNotExists(userId, "COMPLETE_ALL_MODULES_IN_COURSE");
    }
  }

  private void checkMultipleCoursesCompleted(Long userId) {
    long completedCourses = userProgressService.countCompletedEntities(userId, EntityType.COURSE);
    if (completedCourses >= 3) {
      awardAchievementIfNotExists(userId, "COMPLETE_3_COURSES");
    }
    if (completedCourses >= 5) {
      awardAchievementIfNotExists(userId, "COMPLETE_5_COURSES");
    }
    if (completedCourses >= 10) {
      awardAchievementIfNotExists(userId, "COMPLETE_10_COURSES");
    }
  }

  private void checkMultipleRoadmapsCompleted(Long userId) {
    long completedRoadmaps = userProgressService.countCompletedEntities(userId, EntityType.ROADMAP);
    if (completedRoadmaps >= 1) {
      awardAchievementIfNotExists(userId, "COMPLETE_1_ROADMAP");
    }
    if (completedRoadmaps >= 3) {
      awardAchievementIfNotExists(userId, "COMPLETE_3_ROADMAPS");
    }
    if (completedRoadmaps >= 5) {
      awardAchievementIfNotExists(userId, "COMPLETE_5_ROADMAPS");
    }
  }

  private void checkTotalPointsAchievements(Long userId) {
    int totalPoints = userProgressService.getTotalPoints(userId);
    if (totalPoints >= 100) {
      awardAchievementIfNotExists(userId, "EARN_100_POINTS");
    }
    if (totalPoints >= 500) {
      awardAchievementIfNotExists(userId, "EARN_500_POINTS");
    }
    if (totalPoints >= 1000) {
      awardAchievementIfNotExists(userId, "EARN_1000_POINTS");
    }
  }

  private void checkTotalCreditsAchievements(Long userId) {
    int totalCredits = userProgressService.getTotalCredits(userId);
    if (totalCredits >= 10) {
      awardAchievementIfNotExists(userId, "EARN_10_CREDITS");
    }
    if (totalCredits >= 50) {
      awardAchievementIfNotExists(userId, "EARN_50_CREDITS");
    }
    if (totalCredits >= 100) {
      awardAchievementIfNotExists(userId, "EARN_100_CREDITS");
    }
  }

  private void checkConsecutiveDaysAchievements(Long userId) {
    int consecutiveDays = userProgressService.getConsecutiveDaysActive(userId);
    if (consecutiveDays >= 3) {
      awardAchievementIfNotExists(userId, "ACTIVE_3_CONSECUTIVE_DAYS");
    }
    if (consecutiveDays >= 7) {
      awardAchievementIfNotExists(userId, "ACTIVE_7_CONSECUTIVE_DAYS");
    }
    if (consecutiveDays >= 30) {
      awardAchievementIfNotExists(userId, "ACTIVE_30_CONSECUTIVE_DAYS");
    }
  }

  private void awardAchievementIfNotExists(Long userId, String achievementName) {
    AchievementResponse response = achievementService.getAchievementByName(achievementName);

    Achievement achievement = achievementMapper.toEntity(response);

    if (achievement != null && !userHasAchievement(userId, achievement.getId())) {
      achievementService.awardAchievement(userId, achievement.getId());
    }
  }

  private boolean userHasAchievement(Long userId, Long achievementId) {
    return achievementService.getUserAchievements(userId).stream()
        .anyMatch(ua -> ua.getAchievement().getId().equals(achievementId));
  }
}