package com.cortex.backend.education.course.internal;

import com.cortex.backend.core.common.SlugUtils;
import com.cortex.backend.education.course.api.CourseService;
import com.cortex.backend.education.course.api.dto.CourseRequest;
import com.cortex.backend.education.course.api.dto.CourseResponse;
import com.cortex.backend.education.course.api.dto.CourseUpdateRequest;
import com.cortex.backend.core.domain.Course;
import com.cortex.backend.core.domain.Tag;
import com.cortex.backend.education.internal.TagRepository;
import com.cortex.backend.media.api.MediaService;
import com.cortex.backend.core.domain.Media;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

  private final CourseRepository courseRepository;
  private final CourseMapper courseMapper;
  private final MediaService mediaService;
  private final SlugUtils slugUtils;
  private final TagRepository tagRepository;

  private static final String COURSE_IMAGE_UPLOAD_PATH = "courses";
  private static final String COURSE_NOT_FOUND_MESSAGE = "Course not found";

  @Override
  @Transactional(readOnly = true)
  public List<CourseResponse> getAllCourses() {
    return StreamSupport.stream(courseRepository.findAll().spliterator(), false)
        .map(courseMapper::toCourseResponse)
        .toList();
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
    setCourseRelations(course, request);
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

    setCourseRelations(existingCourse, request);
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

  private void setCourseRelations(Course course, CourseRequest request) {
    setCourseTags(course, request.getTagIds());
  }

  private void setCourseRelations(Course course, CourseUpdateRequest request) {
    setCourseTags(course, request.getTagIds());
  }

  private void setCourseTags(Course course, Set<Long> tagIds) {
    if (tagIds != null) {
      Set<Tag> tags = StreamSupport.stream(tagRepository.findAllById(tagIds).spliterator(), false)
          .collect(Collectors.toSet());
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


}