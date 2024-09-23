package com.cortex.backend.education.lesson.internal;

import com.cortex.backend.education.lesson.domain.Lesson;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends CrudRepository<Lesson, Long> {

  /**
   * Find a lesson by its slug.
   *
   * @param slug the slug of the lesson
   * @return the lesson
   */
  Optional<Lesson> findBySlug(String slug);

}
