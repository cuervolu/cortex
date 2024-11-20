package com.cortex.backend.education.progress.internal;

import com.cortex.backend.core.domain.EntityType;
import com.cortex.backend.education.course.api.CourseService;
import com.cortex.backend.education.exercise.api.ExerciseRelationResolver;
import com.cortex.backend.education.lesson.api.LessonService;
import com.cortex.backend.education.module.api.ModuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntityRelationService {

  private final CourseService courseService;
  private final ModuleService moduleService;
  private final LessonService lessonService;
  private final ExerciseRelationResolver exerciseRelationResolver;


  public Long findRelatedRoadmapId(Long entityId, EntityType entityType) {
    try {
      return switch (entityType) {
        case ROADMAP -> entityId;
        case COURSE -> courseService.getRoadmapIdForCourse(entityId);
        case MODULE -> {
          Long courseId = moduleService.getCourseIdForModule(entityId);
          yield courseService.getRoadmapIdForCourse(courseId);
        }
        case LESSON -> {
          Long moduleId = lessonService.getModuleIdForLesson(entityId);
          Long courseId = moduleService.getCourseIdForModule(moduleId);
          yield courseService.getRoadmapIdForCourse(courseId);
        }
        case EXERCISE -> {
          Long lessonId = exerciseRelationResolver.getLessonIdForExercise(entityId);
          Long moduleId = lessonService.getModuleIdForLesson(lessonId);
          Long courseId = moduleService.getCourseIdForModule(moduleId);
          yield courseService.getRoadmapIdForCourse(courseId);
        }
        default -> null;
      };
    } catch (Exception e) {
      log.warn("Error finding related roadmap ID for entity {} of type {}: {}",
          entityId, entityType, e.getMessage());
      return null;
    }
  }

  public Long findRelatedCourseId(Long entityId, EntityType entityType) {
    try {
      return switch (entityType) {
        case COURSE -> entityId;
        case MODULE -> moduleService.getCourseIdForModule(entityId);
        case LESSON -> {
          Long moduleId = lessonService.getModuleIdForLesson(entityId);
          yield moduleService.getCourseIdForModule(moduleId);
        }
        case EXERCISE -> {
          Long lessonId = exerciseRelationResolver.getLessonIdForExercise(entityId);
          Long moduleId = lessonService.getModuleIdForLesson(lessonId);
          yield moduleService.getCourseIdForModule(moduleId);
        }
        default -> null;
      };
    } catch (Exception e) {
      log.warn("Error finding related course ID for entity {} of type {}: {}",
          entityId, entityType, e.getMessage());
      return null;
    }
  }
}