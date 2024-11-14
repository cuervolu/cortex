package com.cortex.backend.education.course.api;

import com.cortex.backend.core.domain.Course;
import com.cortex.backend.core.domain.Roadmap;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {

  Optional<Course> findBySlug(String slug);

  /**
   * Count the number of courses in a roadmap.
   *
   * @param roadmap the roadmap
   * @return the count of courses in the roadmap
   */
  long countByRoadmapsContaining(Roadmap roadmap);

  @Query("""
      SELECT  course
      FROM Course course
      WHERE course.isPublished = true
      """)
  Page<Course> findAllPublishedCourses(Pageable pageable);

  Page<Course> findAll(Pageable pageable);

  @Query("""
          SELECT c FROM Course c
          WHERE (:includeUnpublished = true OR c.isPublished = true)
          AND NOT EXISTS (
              SELECT 1 FROM Roadmap r
              JOIN r.courses rc
              WHERE r.id = :roadmapId
              AND rc = c
          )
          ORDER BY c.displayOrder ASC
      """)
  Page<Course> findAvailableCoursesForRoadmap(
      @Param("roadmapId") Long roadmapId,
      @Param("includeUnpublished") boolean includeUnpublished,
      Pageable pageable);
}