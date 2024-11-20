package com.cortex.backend.engine.internal.mappers;

import com.cortex.backend.core.domain.Exercise;
import com.cortex.backend.engine.api.dto.CreateExercise;
import com.cortex.backend.engine.api.dto.ExerciseResponse;
import com.cortex.backend.engine.api.dto.UpdateExercise;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

  @Mapping(source = "lessonId", target = "lesson.id")
  @Mapping(source = "lastGithubSync", target = "lastGithubSync")
  @Mapping(target = "solutions", ignore = true)
  @Mapping(target = "status", constant = "DRAFT")
  @Mapping(target = "pendingLessonSlug", ignore = true)
  @Mapping(target = "pendingCreator", ignore = true)
  Exercise createExerciseDtoToExercise(CreateExercise dto);

  @Mapping(source = "lesson.id", target = "lessonId")
  @Mapping(source = "solutions", target = "solutionResponses")
  ExerciseResponse exerciseToExerciseResponse(Exercise exercise);

  @Mapping(source = "lessonId", target = "lesson.id")
  @Mapping(target = "lastGithubSync", ignore = true)
  @Mapping(target = "solutions", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "pendingLessonSlug", ignore = true)
  @Mapping(target = "pendingCreator", ignore = true)
  void updateExerciseFromDto(UpdateExercise dto, @MappingTarget Exercise exercise);

  default Set<Long> mapPrerequisites(Set<String> prerequisites) {
    if (prerequisites == null) return new HashSet<>();
    return prerequisites.stream()
        .map(Long::parseLong)
        .collect(Collectors.toSet());
  }
}