package com.cortex.backend.education.course.internal;

import com.cortex.backend.core.common.PageResponse;
import com.cortex.backend.core.common.SlugUtils;
import com.cortex.backend.core.domain.BaseEntity;
import com.cortex.backend.core.domain.EntityType;
import com.cortex.backend.education.course.api.CourseRepository;
import com.cortex.backend.education.course.api.CourseService;
import com.cortex.backend.education.course.api.dto.CourseRequest;
import com.cortex.backend.education.course.api.dto.CourseResponse;
import com.cortex.backend.education.course.api.dto.CourseUpdateRequest;
import com.cortex.backend.core.domain.Course;
import com.cortex.backend.core.domain.Tag;
import com.cortex.backend.education.module.api.ModuleRepository;
import com.cortex.backend.education.progress.api.UserProgressService;
import com.cortex.backend.education.tags.api.dto.TagDTO;
import com.cortex.backend.education.tags.internal.TagService;
import com.cortex.backend.media.api.MediaService;
import com.cortex.backend.core.domain.Media;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class CourseServiceImpl implements CourseService {

  private final CourseRepository courseRepository;
  private final ModuleRepository moduleRepository;
  private final UserProgressService userProgressService;
  private final CourseMapper courseMapper;
  private final MediaService mediaService;
  private final SlugUtils slugUtils;
  private final TagService tagService;

  private static final String COURSE_IMAGE_UPLOAD_PATH = "courses";
  private static final String COURSE_NOT_FOUND_MESSAGE = "Course not found";

  @Override
  @Transactional(readOnly = true)
  public PageResponse<CourseResponse> getAllCourses(int page, int size) {

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

    Page<Course> courses = courseRepository.findAllPublishedCourses(pageable);

    List<CourseResponse> response = courses.stream()
        .map(courseMapper::toCourseResponse)
        .toList();

    return new PageResponse<>(
        response, courses.getNumber(), courses.getSize(), courses.getTotalElements(),
        courses.getTotalPages(), courses.isFirst(), courses.isLast()
    );
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<CourseResponse> getCourseById(Long id) {
    return courseRepository.findById(id)
        .map(courseMapper::toCourseResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<CourseResponse> getCourseBySlug(String slug) {
    return courseRepository.findBySlug(slug)
        .map(courseMapper::toCourseResponse);
  }

  @Override
  @Transactional
  public CourseResponse createCourse(CourseRequest request) {
    Course course = courseMapper.toCourse(request);
    course.setSlug(generateUniqueSlug(request.getName()));
    setCourseTags(course, request.getTags());
    Course savedCourse = courseRepository.save(course);
    return courseMapper.toCourseResponse(savedCourse);
  }

  @Override
  @Transactional
  public CourseResponse updateCourse(Long id, CourseUpdateRequest request) {
    Course existingCourse = courseRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(COURSE_NOT_FOUND_MESSAGE));

    if (request.getName() != null) {
      existingCourse.setName(request.getName());
      existingCourse.setSlug(generateUniqueSlug(request.getName(), existingCourse.getSlug()));
    }
    if (request.getDescription() != null) {
      existingCourse.setDescription(request.getDescription());
    }

    if (request.getTags() != null) {
      setCourseTags(existingCourse, request.getTags());
    }

    if (request.getIsPublished() != null) {
      existingCourse.setIsPublished(request.getIsPublished());
    }

    Course updatedCourse = courseRepository.save(existingCourse);
    return courseMapper.toCourseResponse(updatedCourse);
  }

  @Override
  @Transactional
  public void deleteCourse(Long id) {
    Course course = courseRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(COURSE_NOT_FOUND_MESSAGE));
    courseRepository.delete(course);
  }

  private String generateUniqueSlug(String name) {
    return slugUtils.generateUniqueSlug(name,
        slug -> courseRepository.findBySlug(slug).isPresent());
  }

  private String generateUniqueSlug(String name, String currentSlug) {
    return slugUtils.generateUniqueSlug(name,
        slug -> !slug.equals(currentSlug) && courseRepository.findBySlug(slug).isPresent());
  }

  private void setCourseTags(Course course, Set<TagDTO> tagDTOs) {
    if (tagDTOs != null) {
      Set<Tag> tags = tagService.getOrCreateTags(tagDTOs);
      course.setTags(tags);
    }
  }

  private void handleImageUpload(Course course, MultipartFile image, String altText)
      throws IOException {
    if (image != null && !image.isEmpty()) {
      Media uploadedMedia = mediaService.uploadMedia(image, altText, COURSE_IMAGE_UPLOAD_PATH,
          course.getSlug());
      course.setImage(uploadedMedia);
    }
  }

  @Override
  @Transactional
  public CourseResponse uploadCourseImage(Long id, MultipartFile image, String altText)
      throws IOException {
    Course course = courseRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(COURSE_NOT_FOUND_MESSAGE));

    handleImageUpload(course, image, altText);
    Course updateCourse = courseRepository.save(course);
    return courseMapper.toCourseResponse(updateCourse);
  }

  @Override
  public Long getRoadmapIdForCourse(Long courseId) {
    return courseRepository.findById(courseId)
        .flatMap(course -> course.getRoadmaps().stream().findFirst().map(BaseEntity::getId))
        .orElseThrow(
            () -> new EntityNotFoundException("Course not found or not associated with a roadmap"));
  }

  @Override
  public boolean areAllModulesCompleted(Long userId, Long courseId) {
    Course course = courseRepository.findById(courseId)
        .orElseThrow(() -> new EntityNotFoundException(COURSE_NOT_FOUND_MESSAGE));

    long totalModules = moduleRepository.countByCourse(course);
    long completedModules = course.getModuleEntities().stream()
        .filter(module -> userProgressService.isEntityCompleted(userId, module.getId(),
            EntityType.MODULE))
        .count();

    return totalModules == completedModules;
  }


}