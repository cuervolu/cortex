package com.cortex.backend.education.lesson.internal;

import com.cortex.backend.core.domain.Exercise;
import com.cortex.backend.core.domain.Lesson;
import com.cortex.backend.education.lesson.api.dto.LessonRequest;
import com.cortex.backend.education.lesson.api.dto.LessonResponse;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface LessonMapper {

  @Mapping(target = "moduleId", source = "moduleEntity.id")
  @Mapping(target = "moduleName", source = "moduleEntity.name")
  @Mapping(target = "exerciseIds", source = "exercises", qualifiedByName = "exercisesToIds")
  @Mapping(target = "displayOrder", source = "displayOrder")
  LessonResponse toLessonResponse(Lesson lesson);

  @Mapping(target = "isPublished", source = "published")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "moduleEntity", ignore = true)
  @Mapping(target = "exercises", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "displayOrder", source = "displayOrder")
  Lesson toLesson(LessonRequest lessonRequest);

  @Named("exercisesToIds")
  default Set<Long> exercisesToIds(Set<Exercise> exercises) {
    return exercises != null ? exercises.stream()
        .map(Exercise::getId)
        .collect(Collectors.toSet()) : null;
  }
}