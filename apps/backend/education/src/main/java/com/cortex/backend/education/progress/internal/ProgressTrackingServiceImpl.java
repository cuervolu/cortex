package com.cortex.backend.education.progress.internal;

import com.cortex.backend.core.domain.EntityType;
import com.cortex.backend.core.domain.UserProgress;
import com.cortex.backend.education.progress.api.LessonCompletedEvent;
import com.cortex.backend.education.progress.api.ProgressTrackingService;
import com.cortex.backend.education.progress.api.UserProgressService;
import com.cortex.backend.education.achievement.api.AchievementService;
import com.cortex.backend.education.lesson.api.LessonService;
import com.cortex.backend.education.module.api.ModuleService;
import com.cortex.backend.education.course.api.CourseService;
import com.cortex.backend.education.roadmap.api.RoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProgressTrackingServiceImpl implements ProgressTrackingService {

  private final UserProgressService userProgressService;
  private final AchievementService achievementService;
  private final LessonService lessonService;
  private final ModuleService moduleService;
  private final CourseService courseService;
  private final RoadmapService roadmapService;

  @Override
  @Transactional
  public UserProgress trackProgress(Long userId, Long entityId, EntityType entityType) {
    UserProgress progress = userProgressService.saveProgress(userId, entityId, entityType);
    checkAndUpdateParentProgress(userId, entityId, entityType);
    checkAndAwardAchievements(userId, entityType);
    return progress;
  }

  @Override
  public void checkAndUpdateParentProgress(Long userId, Long entityId, EntityType entityType) {
    switch (entityType) {
      case LESSON:
        checkModuleCompletion(userId, lessonService.getModuleIdForLesson(entityId));
        break;
      case MODULE:
        checkCourseCompletion(userId, moduleService.getCourseIdForModule(entityId));
        break;
      case COURSE:
        checkRoadmapCompletion(userId, courseService.getRoadmapIdForCourse(entityId));
        break;
      case ROADMAP:
        // Roadmap is the highest level, no parent to update
        break;
    }
  }

  @Override
  public boolean isEntityCompleted(Long userId, Long entityId, EntityType entityType) {
    return userProgressService.isEntityCompleted(userId, entityId, entityType);
  }

  private void checkModuleCompletion(Long userId, Long moduleId) {
    if (moduleService.areAllLessonsCompleted(userId, moduleId)) {
      trackProgress(userId, moduleId, EntityType.MODULE);
    }
  }

  private void checkCourseCompletion(Long userId, Long courseId) {
    if (courseService.areAllModulesCompleted(userId, courseId)) {
      trackProgress(userId, courseId, EntityType.COURSE);
    }
  }

  private void checkRoadmapCompletion(Long userId, Long roadmapId) {
    if (roadmapService.areAllCoursesCompleted(userId, roadmapId)) {
      trackProgress(userId, roadmapId, EntityType.ROADMAP);
    }
  }

  @EventListener
  @Transactional
  public void handleLessonCompletedEvent(LessonCompletedEvent event) {
    trackProgress(event.getUserId(), event.getLessonId(), EntityType.LESSON);
  }

  private void checkAndAwardAchievements(Long userId, EntityType entityType) {
    // Aquí implementarías la lógica para verificar y otorgar logros
    // basados en el tipo de entidad completada y posiblemente otras métricas
  }
}