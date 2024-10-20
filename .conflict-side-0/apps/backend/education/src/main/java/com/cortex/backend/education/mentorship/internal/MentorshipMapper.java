package com.cortex.backend.education.mentorship.internal;

import com.cortex.backend.education.mentorship.api.dto.MentorResponse;
import com.cortex.backend.education.mentorship.api.dto.MentorshipResponse;
import com.cortex.backend.education.mentorship.api.dto.MentorshipRequestResponse;
import com.cortex.backend.core.domain.Mentorship;
import com.cortex.backend.core.domain.MentorshipRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MentorshipMapper {

  @Mapping(target = "mentorId", source = "mentor.id")
  @Mapping(target = "menteeId", source = "mentee.id")
  @Mapping(target = "feedbackRating", source = "feedbackRating")
  @Mapping(target = "feedbackComments", source = "feedbackComments")
  MentorshipResponse toMentorshipDTO(Mentorship mentorship);

  @Mapping(target = "userId", source = "user.id")
  MentorshipRequestResponse toMentorshipRequestResponse(MentorshipRequest mentorshipRequest);

  @Mapping(target = "id", source = "id")
  @Mapping(target = "area", source = "area")
  @Mapping(target = "userResponse", ignore = true)
  MentorResponse toMentorResponse(MentorshipRequest mentorshipRequest);
}