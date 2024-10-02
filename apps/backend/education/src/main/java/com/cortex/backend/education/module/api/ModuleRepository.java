package com.cortex.backend.education.module.api;

import com.cortex.backend.core.domain.Course;
import com.cortex.backend.core.domain.ModuleEntity;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends
    CrudRepository<ModuleEntity, Long> {

  Optional<ModuleEntity> findBySlug(String slug);

  /**
   * Count the number of modules in a course.
   *
   * @param course the course
   * @return the count of modules in the course
   */
  long countByCourse(Course course);
}
