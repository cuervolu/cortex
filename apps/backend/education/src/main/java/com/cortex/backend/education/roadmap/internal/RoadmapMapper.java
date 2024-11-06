package com.cortex.backend.education.roadmap.internal;

import com.cortex.backend.core.domain.Course;
import com.cortex.backend.core.domain.EntityType;
import com.cortex.backend.core.domain.Exercise;
import com.cortex.backend.core.domain.Lesson;
import com.cortex.backend.core.domain.ModuleEntity;
import com.cortex.backend.core.domain.Tag;
import com.cortex.backend.education.progress.api.UserProgressService;
import com.cortex.backend.education.roadmap.api.dto.RoadmapCourseDTO;
import com.cortex.backend.education.roadmap.api.dto.RoadmapDetails;
import com.cortex.backend.education.roadmap.api.dto.RoadmapExerciseDTO;
import com.cortex.backend.education.roadmap.api.dto.RoadmapLessonDTO;
import com.cortex.backend.education.roadmap.api.dto.RoadmapModuleDTO;
import com.cortex.backend.education.roadmap.api.dto.RoadmapRequest;
import com.cortex.backend.education.roadmap.api.dto.RoadmapResponse;
import com.cortex.backend.core.domain.Roadmap;
import com.cortex.backend.core.domain.Media;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Context;
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

  @Mapping(target = "imageUrl", source = "roadmap.image", qualifiedByName = "mediaToUrl")
  @Mapping(target = "courses", expression = "java(mapCourses(roadmap, userId, userProgressService))")
  @Mapping(target = "tagNames", source = "roadmap.tags", qualifiedByName = "tagsToNamesList")
  @Mapping(target = "isPublished", source = "roadmap.published")
  RoadmapDetails toRoadmapDetails(Roadmap roadmap, @Context Long userId, @Context UserProgressService userProgressService);

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

  default List<RoadmapCourseDTO> mapCourses(Roadmap roadmap, Long userId, UserProgressService userProgressService) {
    return roadmap.getCourses() != null ? roadmap.getCourses().stream()
        .map(course -> new RoadmapCourseDTO(
            course.getId(),
            course.getName(),
            course.getDescription(),
            mediaToUrl(course.getImage()),
            course.getSlug(),
            tagsToNamesList(course.getTags()),
            modulesToDetailedResponses(course.getModuleEntities(), userId, userProgressService)
        ))
        .toList() : null;
  }


  @Named("modulesToDetailedResponses")
  default List<RoadmapModuleDTO> modulesToDetailedResponses(Set<ModuleEntity> modules, Long userId, UserProgressService userProgressService) {
    return modules != null ? modules.stream()
        .filter(ModuleEntity::getIsPublished)
        .map(module -> new RoadmapModuleDTO(
            module.getId(),
            module.getName(),
            module.getDescription(),
            module.getSlug(),
            module.getLessons().size(),
            lessonsToDetailedResponses(module.getLessons(), userId, userProgressService)
        ))
        .toList() : null;
  }

  @Named("lessonsToDetailedResponses")
  default List<RoadmapLessonDTO> lessonsToDetailedResponses(Set<Lesson> lessons, @Context Long userId, @Context UserProgressService userProgressService) {
    return lessons != null ? lessons.stream()
        .filter(Lesson::getIsPublished)
        .map(lesson -> new RoadmapLessonDTO(
            lesson.getId(),
            lesson.getName(),
            lesson.getSlug(),
            lesson.getCredits(),
            exercisesToDetailedResponses(lesson.getExercises(), userId, userProgressService)
        ))
        .toList() : null;
  }

  @Named("exercisesToDetailedResponses")
  default List<RoadmapExerciseDTO> exercisesToDetailedResponses(Set<Exercise> exercises, @Context Long userId, @Context UserProgressService userProgressService) {
    return exercises != null ? exercises.stream()
        .map(exercise -> RoadmapExerciseDTO.builder()
            .id(exercise.getId())
            .title(exercise.getTitle())
            .slug(exercise.getSlug())
            .points(exercise.getPoints())
            .completed(userProgressService.isEntityCompleted(userId, exercise.getId(), EntityType.EXERCISE))
            .build())
        .toList() : null;
  }
}