package com.cortex.backend.engine.api;

import com.cortex.backend.core.domain.Exercise;
import com.cortex.backend.core.domain.Solution;
import com.cortex.backend.core.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SolutionRepository extends CrudRepository<Solution, Long> {
  Optional<Solution> findByUserAndExercise(User user, Exercise exercise);
}