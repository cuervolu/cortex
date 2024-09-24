package com.cortex.backend.education.lesson.internal;

import com.cortex.backend.common.SlugUtils;
import com.cortex.backend.education.lesson.api.LessonService;
import com.cortex.backend.education.lesson.api.dto.LessonRequest;
import com.cortex.backend.education.lesson.api.dto.LessonResponse;
import com.cortex.backend.education.lesson.api.dto.LessonUpdateRequest;
import com.cortex.backend.education.lesson.domain.Lesson;
import com.cortex.backend.education.module.domain.ModuleEntity;
import com.cortex.backend.education.module.internal.ModuleRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

  private final LessonRepository lessonRepository;
  private final LessonMapper lessonMapper;
  private final ModuleRepository moduleRepository;
  private final SlugUtils slugUtils;

  private static final String LESSON_NOT_FOUND_MESSAGE = "Lesson not found";

  @Override
  @Transactional(readOnly = true)
  public List<LessonResponse> getAllLessons() {
    return StreamSupport.stream(lessonRepository.findAll().spliterator(), false)
        .map(lessonMapper::toLessonResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<LessonResponse> getLessonById(Long id) {
    return lessonRepository.findById(id)
        .map(lessonMapper::toLessonResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<LessonResponse> getLessonBySlug(String slug) {
    return lessonRepository.findBySlug(slug)
        .map(lessonMapper::toLessonResponse);
  }

  @Override
  @Transactional
  public LessonResponse createLesson(LessonRequest request) {
    Lesson lesson = new Lesson();
    lesson.setName(request.getName());
    lesson.setContent(request.getContent());
    lesson.setCredits(request.getCredits());
    lesson.setSlug(generateUniqueSlug(request.getName()));
    setLessonModule(lesson, request.getModuleId());
    Lesson savedLesson = lessonRepository.save(lesson);
    return lessonMapper.toLessonResponse(savedLesson);
  }

  @Override
  @Transactional
  public LessonResponse updateLesson(Long id, LessonUpdateRequest request) {
    Lesson existingLesson = lessonRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(LESSON_NOT_FOUND_MESSAGE));

    if (request.getName() != null) {
      existingLesson.setName(request.getName());
      existingLesson.setSlug(generateUniqueSlug(request.getName(), existingLesson.getSlug()));
    }
    if (request.getContent() != null) {
      existingLesson.setContent(request.getContent());
    }
    if (request.getCredits() != null) {
      existingLesson.setCredits(request.getCredits());
    }
    if (request.getModuleId() != null) {
      setLessonModule(existingLesson, request.getModuleId());
    }

    Lesson updatedLesson = lessonRepository.save(existingLesson);
    return lessonMapper.toLessonResponse(updatedLesson);
  }

  @Override
  @Transactional
  public void deleteLesson(Long id) {
    Lesson lesson = lessonRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(LESSON_NOT_FOUND_MESSAGE));
    lessonRepository.delete(lesson);
  }

  private String generateUniqueSlug(String name) {
    return slugUtils.generateUniqueSlug(name,
        slug -> lessonRepository.findBySlug(slug).isPresent());
  }

  private String generateUniqueSlug(String name, String currentSlug) {
    return slugUtils.generateUniqueSlug(name,
        slug -> !slug.equals(currentSlug) && lessonRepository.findBySlug(slug).isPresent());
  }

  private void setLessonModule(Lesson lesson, Long moduleId) {
    ModuleEntity module = moduleRepository.findById(moduleId)
        .orElseThrow(() -> new EntityNotFoundException("Module not found"));
    lesson.setModuleEntity(module);
  }
}