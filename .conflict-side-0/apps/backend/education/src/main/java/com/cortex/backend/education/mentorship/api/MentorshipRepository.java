package com.cortex.backend.education.mentorship.api;

import com.cortex.backend.core.domain.Mentorship;
import com.cortex.backend.core.domain.MentorshipStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorshipRepository extends JpaRepository<Mentorship, Long> {
  Optional<Mentorship> findByIdAndEndDateIsNull(Long id);
  Page<Mentorship> findByStatus(MentorshipStatus status, Pageable pageable);
}