package com.cortex.backend.engine.api;

import com.cortex.backend.core.domain.Submission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends CrudRepository<Submission, Long> {}
