package com.cortex.backend.education.lesson.internal;

import com.cortex.backend.education.lesson.api.dto.LessonResponse;
import com.cortex.backend.core.domain.Lesson;
import com.cortex.backend.core.domain.Exercise;
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
  LessonResponse toLessonResponse(Lesson lesson);

  @Named("exercisesToIds")
  default Set<Long> exercisesToIds(Set<Exercise> exercises) {
    return exercises != null ? exercises.stream()
        .map(Exercise::getId)
        .collect(Collectors.toSet()) : null;
  }
}