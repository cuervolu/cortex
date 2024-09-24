package com.cortex.backend.education.module.internal;

import com.cortex.backend.education.module.api.dto.ModuleResponse;
import com.cortex.backend.education.module.domain.ModuleEntity;
import com.cortex.backend.education.lesson.domain.Lesson;
import com.cortex.backend.media.domain.Media;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ModuleMapper {

  @Mapping(target = "courseId", source = "course.id")
  @Mapping(target = "courseName", source = "course.name")
  @Mapping(target = "imageUrl", source = "image", qualifiedByName = "mediaToUrl")
  @Mapping(target = "lessonIds", source = "lessons", qualifiedByName = "lessonsToIds")
  ModuleResponse toModuleResponse(ModuleEntity module);

  @Named("mediaToUrl")
  default String mediaToUrl(Media media) {
    return media != null ? media.getUrl() : null;
  }

  @Named("lessonsToIds")
  default Set<Long> lessonsToIds(Set<Lesson> lessons) {
    return lessons != null ? lessons.stream()
        .map(Lesson::getId)
        .collect(Collectors.toSet()) : null;
  }
}