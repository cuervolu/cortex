package com.cortex.backend.education.roadmap.internal;

import com.cortex.backend.core.domain.Course;
import com.cortex.backend.core.domain.EntityType;
import com.cortex.backend.core.domain.Exercise;
import com.cortex.backend.core.domain.Lesson;
import com.cortex.backend.core.domain.Media;
import com.cortex.backend.core.domain.ModuleEntity;
import com.cortex.backend.core.domain.Roadmap;
import com.cortex.backend.core.domain.Tag;
import com.cortex.backend.education.progress.api.UserProgressService;
import com.cortex.backend.education.roadmap.api.dto.RoadmapCourseDTO;
import com.cortex.backend.education.roadmap.api.dto.RoadmapDetails;
import com.cortex.backend.education.roadmap.api.dto.RoadmapExerciseDTO;
import com.cortex.backend.education.roadmap.api.dto.RoadmapLessonDTO;
import com.cortex.backend.education.roadmap.api.dto.RoadmapMentorDTO;
import com.cortex.backend.education.roadmap.api.dto.RoadmapModuleDTO;
import com.cortex.backend.education.roadmap.api.dto.RoadmapRequest;
import com.cortex.backend.education.roadmap.api.dto.RoadmapResponse;
import com.cortex.backend.user.repository.UserRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {UserRepository.class})
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
  @Mapping(target = "mentor", expression = "java(userToMentorDto(roadmap.getCreatedBy(), userRepository))")
  RoadmapDetails toRoadmapDetails(Roadmap roadmap, @Context Long userId,
      @Context UserProgressService userProgressService,
      @Context UserRepository userRepository);

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

  default List<RoadmapCourseDTO> mapCourses(Roadmap roadmap, Long userId,
      UserProgressService userProgressService) {
    return roadmap.getCourses() != null ? roadmap.getCourses().stream()
        .map(course -> new RoadmapCourseDTO(
            course.getId(),
            course.getName(),
            course.getDescription(),
            mediaToUrl(course.getImage()),
            course.getSlug(),
            tagsToNamesList(course.getTags()),
            modulesToDetailedResponses(course.getModuleEntities(), userId, userProgressService),
            course.getDisplayOrder()
        ))
        .toList() : null;
  }


  @Named("userToMentorDto")
  default RoadmapMentorDTO userToMentorDto(Long userId, @Context UserRepository userRepository) {
    if (userId == null) {
      return null;
    }

    return userRepository.findById(userId)
        .map(user -> RoadmapMentorDTO.builder()
            .fullName(user.getFullName())
            .username(user.getUsername())
            .avatarUrl(user.getAvatar() != null ? user.getAvatar().getUrl() : null)
            .build())
        .orElse(null);
  }

  @Named("modulesToDetailedResponses")
  default List<RoadmapModuleDTO> modulesToDetailedResponses(Set<ModuleEntity> modules, Long userId,
      UserProgressService userProgressService) {
    return modules != null ? modules.stream()
        .filter(ModuleEntity::getIsPublished)
        .map(module -> RoadmapModuleDTO.builder()
            .id(module.getId())
            .name(module.getName())
            .description(module.getDescription())
            .slug(module.getSlug())
            .lessonCount(module.getLessons().size())
            .lessons(lessonsToDetailedResponses(module.getLessons(), userId, userProgressService))
            .displayOrder(module.getDisplayOrder())
            .build())
        .toList() : null;
  }

  @Named("lessonsToDetailedResponses")
  default List<RoadmapLessonDTO> lessonsToDetailedResponses(Set<Lesson> lessons,
      @Context Long userId, @Context UserProgressService userProgressService) {
    return lessons != null ? lessons.stream()
        .filter(Lesson::getIsPublished)
        .map(lesson -> RoadmapLessonDTO.builder()
            .id(lesson.getId())
            .name(lesson.getName())
            .slug(lesson.getSlug())
            .credits(lesson.getCredits())
            .exercises(
                exercisesToDetailedResponses(lesson.getExercises(), userId, userProgressService))
            .displayOrder(lesson.getDisplayOrder())
            .build())
        .toList() : null;
  }

  @Named("exercisesToDetailedResponses")
  default List<RoadmapExerciseDTO> exercisesToDetailedResponses(Set<Exercise> exercises,
      @Context Long userId, @Context UserProgressService userProgressService) {
    return exercises != null ? exercises.stream()
        .map(exercise -> RoadmapExerciseDTO.builder()
            .id(exercise.getId())
            .title(exercise.getTitle())
            .slug(exercise.getSlug())
            .points(exercise.getPoints())
            .completed(userProgressService.isEntityCompleted(userId, exercise.getId(),
                EntityType.EXERCISE))
            .displayOrder(exercise.getDisplayOrder())
            .build())
        .toList() : null;
  }
}