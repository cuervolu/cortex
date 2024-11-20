package com.cortex.backend.engine.api;

import com.cortex.backend.core.domain.Exercise;
import com.cortex.backend.core.domain.ExerciseStatus;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long>,
    JpaSpecificationExecutor<Exercise> {

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
   * @param userId   the ID of the user
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

  Page<Exercise> findByStatus(ExerciseStatus status, Pageable pageable);

  long countByStatus(ExerciseStatus status);

  @Query("SELECT new map(e.difficulty as difficulty, COUNT(e) as count) " +
      "FROM Exercise e GROUP BY e.difficulty")
  Map<String, Long> countByDifficulty();

  @Query("SELECT new map(t as tag, COUNT(e) as count) " +
      "FROM Exercise e JOIN e.tags t GROUP BY t")
  Map<String, Long> countByTags();

  @Query("SELECT AVG(e.points) FROM Exercise e WHERE e.status = 'PUBLISHED'")
  double getAveragePoints();

  @Query("SELECT AVG(CASE WHEN s.status >= 1 THEN 1.0 ELSE 0.0 END) " +
      "FROM Exercise e LEFT JOIN e.solutions s " +
      "WHERE e.status = 'PUBLISHED'")
  double getAverageCompletionRate();

  @Modifying
  @Query("UPDATE Exercise e SET e.displayOrder = e.displayOrder - 1 " +
      "WHERE e.lesson.id = :lessonId AND e.displayOrder > :start AND e.displayOrder <= :end")
  void decrementOrdersInRange(Long lessonId, Integer start, Integer end);

  @Modifying
  @Query("UPDATE Exercise e SET e.displayOrder = e.displayOrder + 1 " +
      "WHERE e.lesson.id = :lessonId AND e.displayOrder >= :start AND e.displayOrder < :end")
  void incrementOrdersInRange(Long lessonId, Integer start, Integer end);


  boolean existsBySlug(String slug);


}