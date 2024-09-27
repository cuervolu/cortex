package com.cortex.backend.engine.internal;

import com.cortex.backend.core.domain.Submission;
import com.cortex.backend.engine.api.dto.SubmissionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {

  @Mapping(source = "language.id", target = "languageId") 
  @Mapping(source = "solution.id", target = "solutionId") 
  SubmissionResponse submissionToSubmissionResponse(Submission submission);
}