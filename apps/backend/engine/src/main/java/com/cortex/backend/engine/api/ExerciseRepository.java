package com.cortex.backend.engine.api;

import com.cortex.backend.core.domain.Exercise;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends CrudRepository<Exercise, Long> {

  Optional<Exercise> findByGithubPath(String githubPath);
  
  Optional<Exercise> findBySlug(String slug);
}
