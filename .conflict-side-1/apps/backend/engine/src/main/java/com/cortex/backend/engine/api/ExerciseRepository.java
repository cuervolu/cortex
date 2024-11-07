package com.cortex.backend.engine.api;

import com.cortex.backend.core.domain.Exercise;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends CrudRepository<Exercise, Long> {

  Optional<Exercise> findByGithubPath(String githubPath);

  Optional<Exercise> findBySlug(String slug);

  /**
   * Find all exercises associated with published lessons.
   *
   * @param pageable the pagination information
   * @return a page of exercises whose associated lessons are published
   */
  @Query("SELECT e FROM Exercise e WHERE e.lesson.isPublished = true")
  Page<Exercise> findAllExercisesWithPublishedLessons(Pageable pageable);

  /**
   * Check if all exercises in a lesson are completed by a user.
   *
   * @param userId the ID of the user
   * @param lessonId the ID of the lesson
   * @return true if all exercises in the lesson are completed by the user, false otherwise
   */
  @Query("SELECT CASE WHEN COUNT(e) = 0 THEN true ELSE false END " +
      "FROM Exercise e " +
      "WHERE e.lesson.id = :lessonId " +
      "AND NOT EXISTS (" +
      "    SELECT 1 FROM UserProgress up " +
      "    WHERE up.id.userId = :userId " +
      "    AND up.id.entityId = e.id " +
      "    AND up.id.entityType = 'EXERCISE' " +
      "    AND up.status = 'COMPLETED'" +
      ")")
  boolean areAllExercisesCompletedForLesson(Long userId, Long lessonId);
}