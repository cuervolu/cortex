package com.cortex.backend.education.lesson.api;

import com.cortex.backend.core.common.PageResponse;
import com.cortex.backend.education.lesson.api.dto.LessonRequest;
import com.cortex.backend.education.lesson.api.dto.LessonResponse;
import com.cortex.backend.education.lesson.api.dto.LessonUpdateRequest;
import java.util.Optional;

public interface LessonService {

  PageResponse<LessonResponse> getAllPublishedLessons(int page, int size, String[] sort,
      Long userId);

  PageResponse<LessonResponse> getAllLessons(int page, int size, String[] sort, Long userId);

  Optional<LessonResponse> getLessonById(Long id, Long userId);

  Optional<LessonResponse> getLessonBySlug(String slug, Long userId);

  PageResponse<LessonResponse> getLessonsByModule(Long moduleID, int page, int size, String[] sort, Long userId);

  LessonResponse createLesson(LessonRequest request);

  LessonResponse updateLesson(Long id, LessonUpdateRequest request);

  void deleteLesson(Long id);

  void completeLesson(Long lessonId, Long userId);

  Long getModuleIdForLesson(Long lessonId);
}