package com.cortex.backend.education.roadmap.internal;

import com.cortex.backend.education.course.domain.Course;
import com.cortex.backend.education.domain.Tag;
import com.cortex.backend.education.roadmap.api.dto.RoadmapRequest;
import com.cortex.backend.education.roadmap.api.dto.RoadmapResponse;
import com.cortex.backend.education.roadmap.domain.Roadmap;
import com.cortex.backend.media.domain.Media;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RoadmapMapper {

  @Mapping(target = "imageUrl", source = "image", qualifiedByName = "mediaToUrl")
  @Mapping(target = "tagNames", source = "tags", qualifiedByName = "tagsToNames")
  @Mapping(target = "courseSlugs", source = "courses", qualifiedByName = "coursesToSlugs")
  RoadmapResponse toRoadmapResponse(Roadmap roadmap);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "image", ignore = true)
  @Mapping(target = "tags", ignore = true)
  @Mapping(target = "courses", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Roadmap toRoadmap(RoadmapRequest roadmapRequest);

  @Named("mediaToUrl")
  default String mediaToUrl(Media media) {
    return media != null ? media.getUrl() : null;
  }

  @Named("tagsToNames")
  default Set<String> tagsToNames(Set<Tag> tags) {
    return tags != null ? tags.stream()
        .map(Tag::getName)
        .collect(Collectors.toSet()) : null;
  }

  @Named("coursesToSlugs")
  default Set<String> coursesToSlugs(Set<Course> courses) {
    return courses != null ? courses.stream()
        .map(Course::getSlug)
        .collect(Collectors.toSet()) : null;
  }
}