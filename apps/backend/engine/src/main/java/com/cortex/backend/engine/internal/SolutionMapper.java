package com.cortex.backend.engine.internal;

import com.cortex.backend.core.domain.Solution;
import com.cortex.backend.engine.api.dto.SolutionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolutionMapper {

  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "exercise.id", target = "exerciseId")
  SolutionResponse solutionToSolutionResponse(Solution solution);
  
}
