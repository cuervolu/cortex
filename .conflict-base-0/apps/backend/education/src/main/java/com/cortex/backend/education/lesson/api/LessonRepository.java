package com.cortex.backend.education.lesson.api;

import com.cortex.backend.core.domain.Lesson;
import com.cortex.backend.core.domain.ModuleEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
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

  /**
   * Count the number of lessons in a module.
   *
   * @param moduleEntity the module entity
   * @return the count of lessons in the module
   */
  long countByModuleEntity(ModuleEntity moduleEntity);
  @Query("""
      SELECT  lesson
      FROM Lesson lesson
      WHERE lesson.isPublished = true
      """)
  Page<Lesson> findAllPublishedLessons(Pageable pageable);

  Page<Lesson> findAll(Pageable pageable);
}
