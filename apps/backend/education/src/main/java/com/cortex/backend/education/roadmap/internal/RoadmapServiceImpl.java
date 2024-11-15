package com.cortex.backend.education.roadmap.internal;

import com.cortex.backend.core.common.PageResponse;
import com.cortex.backend.core.common.SlugUtils;
import com.cortex.backend.core.common.SortUtils;
import com.cortex.backend.core.domain.Course;
import com.cortex.backend.core.domain.EntityType;
import com.cortex.backend.core.domain.Media;
import com.cortex.backend.core.domain.Roadmap;
import com.cortex.backend.core.domain.Tag;
import com.cortex.backend.core.domain.User;
import com.cortex.backend.education.course.api.CourseRepository;
import com.cortex.backend.education.course.api.CourseService;
import com.cortex.backend.education.course.api.dto.CourseResponse;
import com.cortex.backend.education.course.internal.CourseMapper;
import com.cortex.backend.education.lesson.api.LessonService;
import com.cortex.backend.education.module.api.ModuleService;
import com.cortex.backend.education.progress.api.ProgressUpdatedEvent;
import com.cortex.backend.education.progress.api.UserProgressService;
import com.cortex.backend.education.progress.internal.EntityRelationService;
import com.cortex.backend.education.roadmap.api.RoadmapRepository;
import com.cortex.backend.education.roadmap.api.RoadmapService;
import com.cortex.backend.education.roadmap.api.dto.CourseAssignment;
import com.cortex.backend.education.roadmap.api.dto.RoadmapDetails;
import com.cortex.backend.education.roadmap.api.dto.RoadmapRequest;
import com.cortex.backend.education.roadmap.api.dto.RoadmapResponse;
import com.cortex.backend.education.roadmap.api.dto.RoadmapUpdateRequest;
import com.cortex.backend.education.tags.internal.TagService;
import com.cortex.backend.media.api.MediaService;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoadmapServiceImpl implements RoadmapService {

  private final RoadmapRepository roadmapRepository;
  private final RoadmapMapper roadmapMapper;
  private final MediaService mediaService;
  private final SlugUtils slugUtils;
  private final TagService tagService;
  private final CourseRepository courseRepository;
  private final UserProgressService userProgressService;
  private final CourseMapper courseMapper;
  private final CourseService courseService;
  private final ModuleService moduleService;
  private final LessonService lessonService;
  private final EntityRelationService entityRelationService;
  private final CacheManager cacheManager;

  private static final String ROADMAP_IMAGE_UPLOAD_PATH = "roadmaps";
  private static final String ROADMAP_NOT_FOUND_MESSAGE = "Roadmap not found";

  @Override
  @Transactional(readOnly = true)
  public PageResponse<RoadmapResponse> getAllPublishedRoadmaps(int page, int size, String[] sort) {
    Sort sorting = SortUtils.parseSort(sort);
    Pageable pageable = PageRequest.of(page, size, sorting);
    Page<Roadmap> roadmaps = roadmapRepository.findAllPublishedRoadmaps(pageable);

    List<RoadmapResponse> response = roadmaps.stream()
        .map(roadmapMapper::toRoadmapResponse)
        .toList();

    return new PageResponse<>(
        response, roadmaps.getNumber(), roadmaps.getSize(), roadmaps.getTotalElements(),
        roadmaps.getTotalPages(), roadmaps.isFirst(), roadmaps.isLast()
    );
  }

  @Override
  public PageResponse<RoadmapResponse> getAllRoadmaps(int page, int size, String[] sort) {
    Sort sorting = SortUtils.parseSort(sort);
    Pageable pageable = PageRequest.of(page, size, sorting);
    Page<Roadmap> roadmaps = roadmapRepository.findAll(pageable);

    List<RoadmapResponse> response = roadmaps.stream()
        .map(roadmapMapper::toRoadmapResponse)
        .toList();

    return new PageResponse<>(
        response, roadmaps.getNumber(), roadmaps.getSize(), roadmaps.getTotalElements(),
        roadmaps.getTotalPages(), roadmaps.isFirst(), roadmaps.isLast()
    );
  }

  @Override
  @Cacheable(value = "roadmaps", key = "#slug + '_' + #user.id")
  @Transactional(readOnly = true)
  public Optional<RoadmapDetails> getRoadmapBySlug(String slug, User user) {
    if (user.hasAnyRole("ADMIN", "MODERATOR")) {
      return roadmapRepository.findBySlugWithDetailsWithAdminRole(slug)
          .map(roadmap -> roadmapMapper.toRoadmapDetails(roadmap, user.getId(),
              userProgressService));
    }

    return roadmapRepository.findBySlugWithDetails(slug)
        .map(roadmap -> roadmapMapper.toRoadmapDetails(roadmap, user.getId(), userProgressService));
  }

  @Override
  public Optional<RoadmapResponse> getRoadmapById(Long id) {
    return roadmapRepository.findById(id)
        .map(roadmapMapper::toRoadmapResponse);
  }

  @Override
  @Transactional
  public RoadmapResponse createRoadmap(RoadmapRequest request) {
    Roadmap roadmap = roadmapMapper.toRoadmap(request);
    roadmap.setSlug(generateUniqueSlug(request.getTitle()));
    setRoadmapRelations(roadmap, request);
    Roadmap savedRoadmap = roadmapRepository.save(roadmap);
    return roadmapMapper.toRoadmapResponse(savedRoadmap);
  }

  @CacheEvict(value = "roadmaps", key = "#id")
  @Override
  @Transactional
  public RoadmapResponse updateRoadmap(Long id, RoadmapUpdateRequest request) {
    Roadmap existingRoadmap = roadmapRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(ROADMAP_NOT_FOUND_MESSAGE));

    if (request.getTitle() != null) {
      existingRoadmap.setTitle(request.getTitle());
      if (!existingRoadmap.getTitle().equals(request.getTitle())) {
        existingRoadmap.setSlug(generateUniqueSlug(request.getTitle(), existingRoadmap.getSlug()));
      }
    }
    if (request.getDescription() != null) {
      existingRoadmap.setDescription(request.getDescription());
    }

    if (request.getIsPublished() != null) {
      existingRoadmap.setPublished(request.getIsPublished());
    }

    setRoadmapRelations(existingRoadmap, request);
    Roadmap updatedRoadmap = roadmapRepository.save(existingRoadmap);
    return roadmapMapper.toRoadmapResponse(updatedRoadmap);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<CourseResponse> getCourseFromRoadmap(String roadmapSlug, String courseSlug) {
    return roadmapRepository.findBySlug(roadmapSlug)
        .flatMap(roadmap -> courseRepository.findBySlug(courseSlug)
            .filter(course -> roadmap.getCourses().contains(course))
            .map(courseMapper::toCourseResponse));
  }

  @Override
  @Transactional
  public void deleteRoadmap(Long id) {
    Roadmap roadmap = roadmapRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(ROADMAP_NOT_FOUND_MESSAGE));
    roadmapRepository.delete(roadmap);
  }

  private String generateUniqueSlug(String title) {
    return slugUtils.generateUniqueSlug(title, roadmapRepository::existsBySlug);
  }

  private String generateUniqueSlug(String title, String currentSlug) {
    return slugUtils.generateUniqueSlug(title,
        slug -> !slug.equals(currentSlug) && roadmapRepository.existsBySlug(slug));
  }


  private void setRoadmapRelations(Roadmap roadmap, RoadmapRequest request) {
    if (request.getTags() != null) {
      Set<Tag> tags = tagService.getOrCreateTags(request.getTags());
      roadmap.setTags(tags);
    }
    setRoadmapCourses(roadmap, request.getCourseIds());
  }

  private void setRoadmapRelations(Roadmap roadmap, RoadmapUpdateRequest request) {
    if (request.getTags() != null) {
      Set<Tag> tags = tagService.getOrCreateTags(request.getTags());
      roadmap.setTags(tags);
    }
    if (request.getCourseIds() != null) {
      setRoadmapCourses(roadmap, request.getCourseIds());
    }
  }

  private void setRoadmapCourses(Roadmap roadmap, Set<Long> courseIds) {
    if (courseIds != null) {
      Set<Course> courses = StreamSupport.stream(
              courseRepository.findAllById(courseIds).spliterator(), false)
          .collect(Collectors.toSet());
      roadmap.setCourses(courses);
    }
  }

  private void handleImageUpload(Roadmap roadmap, MultipartFile image, String altText)
      throws IOException {
    if (image != null && !image.isEmpty()) {
      Media uploadedMedia = mediaService.uploadMedia(image, altText, ROADMAP_IMAGE_UPLOAD_PATH,
          roadmap.getSlug());
      roadmap.setImage(uploadedMedia);
    }
  }

  @Override
  @Transactional
  public RoadmapResponse uploadRoadmapImage(Long id, MultipartFile image, String altText)
      throws IOException {
    Roadmap roadmap = roadmapRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(ROADMAP_NOT_FOUND_MESSAGE));

    handleImageUpload(roadmap, image, altText);
    Roadmap updatedRoadmap = roadmapRepository.save(roadmap);
    return roadmapMapper.toRoadmapResponse(updatedRoadmap);
  }

  @Override
  public boolean areAllCoursesCompleted(Long userId, Long roadmapId) {
    Roadmap roadmap = roadmapRepository.findById(roadmapId)
        .orElseThrow(() -> new EntityNotFoundException(ROADMAP_NOT_FOUND_MESSAGE));

    long totalCourses = courseRepository.countByRoadmapsContaining(roadmap);
    long completedCourses = roadmap.getCourses().stream()
        .filter(course -> userProgressService.isEntityCompleted(userId, course.getId(),
            EntityType.COURSE))
        .count();

    return totalCourses == completedCourses;
  }

  @EventListener
  public void handleProgressUpdated(ProgressUpdatedEvent event) {
    try {
      Long roadmapId = entityRelationService.findRelatedRoadmapId(event.entityId(), event.entityType());
      if (roadmapId != null) {
        Optional.ofNullable(cacheManager.getCache("roadmaps"))
            .ifPresent(cache -> {
              cache.evict(roadmapId);
              log.debug("Invalidated cache for roadmap ID: {}", roadmapId);
            });
      }
    } catch (Exception e) {
      log.error("Error handling progress update event", e);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<PageResponse<CourseResponse>> getAvailableCourses(
      Long roadmapId,
      int page,
      int size,
      String[] sort,
      boolean includeUnpublished) {

    return roadmapRepository.findById(roadmapId)
        .map(roadmap -> {
          Sort sorting = SortUtils.parseSort(sort);
          Pageable pageable = PageRequest.of(page, size, sorting);

          Page<Course> availableCourses = courseRepository.findAvailableCoursesForRoadmap(
              roadmap.getId(),
              includeUnpublished,
              pageable
          );

          List<CourseResponse> courseResponses = availableCourses.stream()
              .map(courseMapper::toCourseResponse)
              .toList();

          return new PageResponse<>(
              courseResponses,
              availableCourses.getNumber(),
              availableCourses.getSize(),
              availableCourses.getTotalElements(),
              availableCourses.getTotalPages(),
              availableCourses.isFirst(),
              availableCourses.isLast()
          );
        });
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<PageResponse<CourseResponse>> getRoadmapCourses(Long roadmapId, int page,
      int size, String[] sort) {
    return roadmapRepository.findById(roadmapId)
        .map(roadmap -> {
          Sort sorting = SortUtils.parseSort(sort);
          Pageable pageable = PageRequest.of(page, size, sorting);

          Page<Course> courses = courseRepository.findByRoadmapsContainingOrderByDisplayOrderAsc(
              roadmap, pageable);

          List<CourseResponse> courseResponses = courses.stream()
              .map(courseMapper::toCourseResponse)
              .toList();

          return new PageResponse<>(
              courseResponses,
              courses.getNumber(),
              courses.getSize(),
              courses.getTotalElements(),
              courses.getTotalPages(),
              courses.isFirst(),
              courses.isLast()
          );
        });
  }

  @Transactional
  @Override
  public void assignCoursesToRoadmap(Long roadmapId, List<CourseAssignment> assignments) {
    Roadmap roadmap = roadmapRepository.findById(roadmapId)
        .orElseThrow(() -> new EntityNotFoundException(ROADMAP_NOT_FOUND_MESSAGE));

    Set<Long> courseIds = assignments.stream()
        .map(CourseAssignment::courseId)
        .collect(Collectors.toSet());

    Map<Long, Course> coursesMap = StreamSupport.stream(
            courseRepository.findAllById(courseIds).spliterator(), false)
        .collect(Collectors.toMap(Course::getId, course -> course));

    assignments.forEach(assignment -> {
      if (!coursesMap.containsKey(assignment.courseId())) {
        throw new EntityNotFoundException("Course not found with id: " + assignment.courseId());
      }
    });

    assignments.forEach(assignment -> {
      Course course = coursesMap.get(assignment.courseId());
      course.setDisplayOrder(assignment.displayOrder());
      courseRepository.save(course);
    });

    roadmap.setCourses(new HashSet<>(coursesMap.values()));
    roadmapRepository.save(roadmap);

    Optional.ofNullable(cacheManager.getCache("roadmaps"))
        .ifPresent(cache -> cache.evict(roadmap.getSlug()));
  }


}