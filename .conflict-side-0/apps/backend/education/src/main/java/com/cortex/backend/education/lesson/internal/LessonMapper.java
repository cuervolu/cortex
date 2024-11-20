package com.cortex.backend.education.lesson.internal;

import com.cortex.backend.core.domain.EntityType;
import com.cortex.backend.core.domain.Exercise;
import com.cortex.backend.core.domain.Lesson;
import com.cortex.backend.education.lesson.api.dto.ExerciseInfo;
import com.cortex.backend.education.lesson.api.dto.LessonRequest;
import com.cortex.backend.education.lesson.api.dto.LessonResponse;
import com.cortex.backend.education.progress.api.UserProgressService;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LessonMapper {

  @Mapping(target = "moduleId", source = "lesson.moduleEntity.id")
  @Mapping(target = "moduleName", source = "lesson.moduleEntity.name")
  @Mapping(target = "exercises", expression = "java(mapExercises(lesson.getExercises(), userId, userProgressService))")
  @Mapping(target = "displayOrder", source = "lesson.displayOrder")
  @Mapping(target = "isPublished", source = "lesson.isPublished")
  LessonResponse toLessonResponse(Lesson lesson, Long userId, UserProgressService userProgressService);

  @Mapping(target = "isPublished", source = "published")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "moduleEntity", ignore = true)
  @Mapping(target = "exercises", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "displayOrder", source = "displayOrder")
  Lesson toLesson(LessonRequest lessonRequest);

  default Set<ExerciseInfo> mapExercises(Set<Exercise> exercises, Long userId, UserProgressService userProgressService) {
    if (exercises == null) return null;

    return exercises.stream()
        .map(exercise -> ExerciseInfo.builder()
            .id(exercise.getId())
            .slug(exercise.getSlug())
            .title(exercise.getTitle())
            .points(exercise.getPoints())
            .displayOrder(exercise.getDisplayOrder())
            .isCompleted(userProgressService.isEntityCompleted(
                userId,
                exercise.getId(),
                EntityType.EXERCISE))
            .build())
        .collect(Collectors.toSet());
  }
}