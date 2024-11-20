package com.cortex.backend.education.mentorship.api;

import com.cortex.backend.core.domain.MentorshipRequest;
import com.cortex.backend.core.domain.MentorshipRequestStatus;
import com.cortex.backend.core.domain.MentorshipRequestType;
import com.cortex.backend.core.domain.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorshipRequestRepository extends CrudRepository<MentorshipRequest, Long> {
  Page<MentorshipRequest> findByStatusAndType(MentorshipRequestStatus status, MentorshipRequestType type, Pageable pageable);

  Optional<MentorshipRequest> findByUserAndStatus(User user, MentorshipRequestStatus status);
}