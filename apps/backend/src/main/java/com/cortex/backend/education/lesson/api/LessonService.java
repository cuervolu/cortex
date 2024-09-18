package com.cortex.backend.education.lesson.api;

import com.cortex.backend.education.lesson.domain.Lesson;
import java.util.List;
import java.util.Optional;

public interface LessonService {

  List<Lesson> getAllLessons();

  Optional<Lesson> getLessonById(Long id);

  Lesson createLesson(Lesson lesson);

  Lesson updateLesson(Lesson lesson);

  void deleteLesson(Long id);
}
