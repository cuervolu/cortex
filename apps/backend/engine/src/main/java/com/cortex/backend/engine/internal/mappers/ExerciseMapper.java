package com.cortex.backend.engine.internal.mappers;

import com.cortex.backend.core.domain.Exercise;
import com.cortex.backend.engine.api.dto.CreateExercise;
import com.cortex.backend.engine.api.dto.ExerciseResponse;
import com.cortex.backend.engine.api.dto.UpdateExercise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

  @Mapping(source = "lessonId", target = "lesson.id")
  @Mapping(source = "lastGithubSync", target = "lastGithubSync")
  @Mapping(target = "solutions", ignore = true) 
  Exercise createExerciseDtoToExercise(CreateExercise dto);

  @Mapping(source = "lesson.id", target = "lessonId")
  @Mapping(source = "solutions", target = "solutionResponses") 
  ExerciseResponse exerciseToExerciseResponse(Exercise exercise);

  @Mapping(source = "lessonId", target = "lesson.id")
  @Mapping(target = "lastGithubSync", ignore = true)
  @Mapping(target = "solutions", ignore = true)
  void updateExerciseFromDto(UpdateExercise dto, @MappingTarget Exercise exercise);
}
