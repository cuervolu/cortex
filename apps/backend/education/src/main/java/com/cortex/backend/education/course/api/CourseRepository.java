package com.cortex.backend.education.course.api;

import com.cortex.backend.core.domain.Course;
import com.cortex.backend.core.domain.Roadmap;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
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

}
