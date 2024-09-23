package com.cortex.backend.education.course.internal;

import com.cortex.backend.education.course.domain.Course;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {

  Optional<Course> findBySlug(String slug);

}
