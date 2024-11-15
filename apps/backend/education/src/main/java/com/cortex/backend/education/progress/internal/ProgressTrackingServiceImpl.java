package com.cortex.backend.education.progress.internal;

import com.cortex.backend.core.domain.EntityType;
import com.cortex.backend.core.domain.Lesson;
import com.cortex.backend.core.domain.UserProgress;
import com.cortex.backend.education.achievement.api.AchievementService;
import com.cortex.backend.education.course.api.CourseService;
import com.cortex.backend.education.exercise.api.ExerciseRelationResolver;
import com.cortex.backend.education.lesson.api.LessonService;
import com.cortex.backend.education.module.api.ModuleService;
import com.cortex.backend.education.progress.api.LessonCompletedEvent;
import com.cortex.backend.education.progress.api.ProgressTrackingService;
import com.cortex.backend.education.progress.api.ProgressUpdatedEvent;
import com.cortex.backend.education.progress.api.UserProgressService;
import com.cortex.backend.education.roadmap.api.RoadmapService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressTrackingServiceImpl implements ProgressTrackingService {
  private final UserProgressService userProgressService;
  private final AchievementService achievementService;
  private final EntityRelationService entityRelationService;
  private final ExerciseRelationResolver exerciseRelationResolver;
  private final ApplicationEventPublisher eventPublisher;
  private final LessonService lessonService;
  private final ModuleService moduleService;
  private final CourseService courseService;
  private final RoadmapService roadmapService;
  private final CacheManager cacheManager;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void trackProgressAndCheckCompletion(Long userId, Long exerciseId, EntityType entityType, Lesson lesson) {
    try {
      UserProgress progress = trackProgress(userId, exerciseId, entityType);

      if (lesson != null) {
        if (exerciseRelationResolver.areAllExercisesCompletedForLesson(userId, lesson.getId())) {
          eventPublisher.publishEvent(new LessonCompletedEvent(lesson.getId(), userId));
        }
      } else {
        log.debug("Exercise {} has no associated lesson, skipping lesson completion check", exerciseId);
      }
    } catch (Exception e) {
      log.error("Error tracking progress for exercise {} and user {}: {}",
          exerciseId, userId, e.getMessage());
      throw e;
    }
  }

  private void validateExerciseState(Long exerciseId, Lesson lesson) {
    if (lesson == null) {
      log.warn("Exercise {} has no associated lesson", exerciseId);
    }
  }

  @Override
  @Transactional
  public UserProgress trackProgress(Long userId, Long entityId, EntityType entityType) {
    UserProgress progress = userProgressService.saveProgress(userId, entityId, entityType);
    checkAndUpdateParentProgress(userId, entityId, entityType);
    checkAndAwardAchievements(userId, entityType);

    // Publishing event for progress update
    ProgressUpdatedEvent event = new ProgressUpdatedEvent(userId, entityId, entityType);
    eventPublisher.publishEvent(event);

    // Invalidating related caches
    invalidateRelatedCaches(entityId, entityType);

    return progress;
  }

  private void invalidateRelatedCaches(Long entityId, EntityType entityType) {
    try {
      Long roadmapId = entityRelationService.findRelatedRoadmapId(entityId, entityType);
      if (roadmapId != null) {
        Optional.ofNullable(cacheManager.getCache("roadmaps"))
            .ifPresent(cache -> {
              cache.evict(roadmapId);
              log.debug("Invalidated cache for roadmap ID: {}", roadmapId);
            });
      }
    } catch (Exception e) {
      log.error("Error invalidating caches for entity: {} of type: {}", entityId, entityType, e);
    }
  }

  @Override
  @Transactional
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
    trackProgress(event.userId(), event.lessonId(), EntityType.LESSON);
  }

  private void checkAndAwardAchievements(Long userId, EntityType entityType) {
    //TODO: Implement achievement logic
  }
}