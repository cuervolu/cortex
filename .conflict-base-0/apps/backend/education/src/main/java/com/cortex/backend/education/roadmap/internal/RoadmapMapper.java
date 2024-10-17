package com.cortex.backend.education.roadmap.internal;

import com.cortex.backend.core.domain.Course;
import com.cortex.backend.core.domain.ModuleEntity;
import com.cortex.backend.core.domain.Tag;
import com.cortex.backend.education.course.api.dto.CourseResponse;
import com.cortex.backend.education.roadmap.api.dto.RoadmapDetails;
import com.cortex.backend.education.roadmap.api.dto.RoadmapRequest;
import com.cortex.backend.education.roadmap.api.dto.RoadmapResponse;
import com.cortex.backend.core.domain.Roadmap;
import com.cortex.backend.core.domain.Media;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RoadmapMapper {

  @Mapping(target = "imageUrl", source = "image", qualifiedByName = "mediaToUrl")
  @Mapping(target = "tagNames", source = "tags", qualifiedByName = "tagsToNamesList")
  @Mapping(target = "courseSlugs", source = "courses", qualifiedByName = "coursesToSlugs")
  @Mapping(target = "isPublished", source = "published")
  RoadmapResponse toRoadmapResponse(Roadmap roadmap);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "image", ignore = true)
  @Mapping(target = "tags", ignore = true)
  @Mapping(target = "courses", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "isPublished", source = "published")
  @Mapping(target = "slug", ignore = true)
  Roadmap toRoadmap(RoadmapRequest roadmapRequest);

  @Mapping(target = "imageUrl", source = "image", qualifiedByName = "mediaToUrl")
  @Mapping(target = "courses", source = "courses", qualifiedByName = "coursesToCourseResponses")
  @Mapping(target = "tagNames", source = "tags", qualifiedByName = "tagsToNamesList")
  @Mapping(target = "isPublished", source = "published")
  RoadmapDetails toRoadmapDetails(Roadmap roadmap);
  @Named("mediaToUrl")
  default String mediaToUrl(Media media) {
    return media != null ? media.getUrl() : null;
  }

  @Named("tagsToNamesList")
  default List<String> tagsToNamesList(Set<Tag> tags) {
    return tags != null ? tags.stream()
        .map(Tag::getName)
        .toList() : null;
  }

  @Named("coursesToSlugs")
  default Set<String> coursesToSlugs(Set<Course> courses) {
    return courses != null ? courses.stream()
        .map(Course::getSlug)
        .collect(Collectors.toSet()) : null;
  }

  @Named("coursesToCourseResponses")
  default List<CourseResponse> coursesToCourseResponses(Set<Course> courses) {
    return courses != null ? courses.stream()
        .map(course -> CourseResponse.builder()
            .id(course.getId())
            .name(course.getName())
            .imageUrl(course.getImage() != null ? course.getImage().getUrl() : null)
            .tagNames(tagsToNamesList(course.getTags()))
            .roadmapSlugs(course.getRoadmaps().stream()
                .map(Roadmap::getSlug)
                .collect(Collectors.toSet()))
            .moduleIds(course.getModuleEntities().stream()
                .map(ModuleEntity::getId)
                .collect(Collectors.toSet()))
            .createdAt(course.getCreatedAt())
            .updatedAt(course.getUpdatedAt())
            .description(course.getDescription())
            .slug(course.getSlug())
            .build())
        .toList() : null;
  }
}