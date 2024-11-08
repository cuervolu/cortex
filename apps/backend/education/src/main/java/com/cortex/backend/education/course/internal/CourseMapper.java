package com.cortex.backend.education.course.internal;

import com.cortex.backend.core.domain.Course;
import com.cortex.backend.core.domain.Media;
import com.cortex.backend.core.domain.ModuleEntity;
import com.cortex.backend.core.domain.Roadmap;
import com.cortex.backend.core.domain.Tag;
import com.cortex.backend.education.course.api.dto.CourseRequest;
import com.cortex.backend.education.course.api.dto.CourseResponse;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CourseMapper {

  @Mapping(target = "imageUrl", source = "image", qualifiedByName = "mediaToUrl")
  @Mapping(target = "roadmapSlugs", source = "roadmaps", qualifiedByName = "roadmapsToSlugs")
  @Mapping(target = "tagNames", source = "tags", qualifiedByName = "tagsToNames")
  @Mapping(target = "moduleIds", source = "moduleEntities", qualifiedByName = "modulesToIds")
  @Mapping(target = "displayOrder", source = "displayOrder")
  CourseResponse toCourseResponse(Course course);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "image", ignore = true)
  @Mapping(target = "roadmaps", ignore = true)
  @Mapping(target = "tags", ignore = true)
  @Mapping(target = "moduleEntities", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "isPublished", source = "published")
  @Mapping(target = "displayOrder", source = "displayOrder")
  @Mapping(target = "slug", ignore = true)
  Course toCourse(CourseRequest courseRequest);

  @Named("mediaToUrl")
  default String mediaToUrl(Media media) {
    return media != null ? media.getUrl() : null;
  }

  @Named("roadmapsToSlugs")
  default Set<String> roadmapsToSlugs(Set<Roadmap> roadmaps) {
    return roadmaps != null ? roadmaps.stream()
        .map(Roadmap::getSlug)
        .collect(Collectors.toSet()) : null;
  }

  @Named("tagsToNames")
  default List<String> tagsToNames(Set<Tag> tags) {
    return tags != null ? tags.stream()
        .map(Tag::getName)
        .toList() : null;
  }

  @Named("modulesToIds")
  default Set<Long> modulesToIds(Set<ModuleEntity> modules) {
    return modules != null ? modules.stream()
        .map(ModuleEntity::getId)
        .collect(Collectors.toSet()) : null;
  }
}