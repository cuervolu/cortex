package com.cortex.backend.education.lesson.api;

import com.cortex.backend.core.common.PageResponse;
import com.cortex.backend.education.lesson.api.dto.LessonRequest;
import com.cortex.backend.education.lesson.api.dto.LessonResponse;
import com.cortex.backend.education.lesson.api.dto.LessonUpdateRequest;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface LessonService {

  PageResponse<LessonResponse> getAllPublishedLessons(int page, int size, String[] sort);

  PageResponse<LessonResponse> getAllLessons(int page, int size, String[] sort);

  Optional<LessonResponse> getLessonById(Long id);

  Optional<LessonResponse> getLessonBySlug(String slug);

  LessonResponse createLesson(LessonRequest lesson);

  LessonResponse updateLesson(Long id, LessonUpdateRequest lessonRequest);

  void deleteLesson(Long id);

  void completeLesson(Long lessonId, Long userId);
  
  Long getModuleIdForLesson(Long lessonId);
  
}
