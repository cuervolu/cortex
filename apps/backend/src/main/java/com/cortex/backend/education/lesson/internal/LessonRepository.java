package com.cortex.backend.education.lesson.internal;

import com.cortex.backend.education.lesson.domain.Lesson;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends CrudRepository<Lesson, Long> {

}
