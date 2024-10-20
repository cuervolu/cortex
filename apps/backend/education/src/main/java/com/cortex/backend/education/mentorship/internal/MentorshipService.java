package com.cortex.backend.education.mentorship.internal;

import static com.cortex.backend.core.domain.MentorshipRequestStatus.APPROVED;
import static com.cortex.backend.core.domain.MentorshipRequestStatus.PENDING;
import static com.cortex.backend.core.domain.MentorshipRequestType.MENTEE_REQUEST;
import static com.cortex.backend.core.domain.MentorshipRequestType.MENTOR_APPLICATION;

import com.cortex.backend.core.common.PageResponse;
import com.cortex.backend.core.common.exception.NoApprovedMentorshipRequestException;
import com.cortex.backend.core.common.exception.UserNotPartOfMentorshipException;
import com.cortex.backend.core.domain.Mentorship;
import com.cortex.backend.core.domain.MentorshipRequest;
import com.cortex.backend.core.domain.MentorshipRequestStatus;
import com.cortex.backend.core.domain.MentorshipRequestType;
import com.cortex.backend.core.domain.MentorshipStatus;
import com.cortex.backend.core.domain.Role;
import com.cortex.backend.core.domain.User;
import com.cortex.backend.education.mentorship.api.MentorshipRepository;
import com.cortex.backend.education.mentorship.api.MentorshipRequestRepository;
import com.cortex.backend.education.mentorship.api.dto.MentorApplicationRequest;
import com.cortex.backend.education.mentorship.api.dto.MentorResponse;
import com.cortex.backend.education.mentorship.api.dto.MentorshipRequestResponse;
import com.cortex.backend.education.mentorship.api.dto.MentorshipResponse;
import com.cortex.backend.education.mentorship.api.dto.MentorshipFeedback;
import com.cortex.backend.user.api.dto.UserResponse;
import com.cortex.backend.user.internal.UserMapper;
import com.cortex.backend.user.repository.RoleRepository;
import com.cortex.backend.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MentorshipService {

  private final MentorshipRepository mentorshipRepository;
  private final MentorshipRequestRepository mentorshipRequestRepository;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final UserMapper userMapper;
  private final MentorshipMapper mentorshipMapper;

  private static final String SORT_BY_CREATED_AT = "createdAt";

  public MentorshipRequestResponse applyToBeMentor(User user,
      MentorApplicationRequest application) {
    MentorshipRequest request = MentorshipRequest.builder()
        .user(user)
        .status(PENDING)
        .area(application.area())
        .reason(application.reason())
        .type(MENTOR_APPLICATION)
        .build();
    MentorshipRequest savedRequest = mentorshipRequestRepository.save(request);
    return mentorshipMapper.toMentorshipRequestResponse(savedRequest);
  }

  @Transactional(readOnly = true)
  public PageResponse<MentorResponse> getAvailableMentors(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(SORT_BY_CREATED_AT).descending());

    Page<MentorshipRequest> mentorRequests = mentorshipRequestRepository.findByStatusAndType(
        MentorshipRequestStatus.APPROVED,
        MentorshipRequestType.MENTOR_APPLICATION,
        pageable
    );

    List<MentorResponse> mentorResponses = mentorRequests.getContent().stream()
        .map(request -> new MentorResponse(
            request.getId(),
            request.getArea(),
            userMapper.toUserResponse(request.getUser())
        ))
        .toList();

    return new PageResponse<>(
        mentorResponses,
        mentorRequests.getNumber(),
        mentorRequests.getSize(),
        mentorRequests.getTotalElements(),
        mentorRequests.getTotalPages(),
        mentorRequests.isFirst(),
        mentorRequests.isLast()
    );
  }


  @Transactional
  public MentorshipResponse createMentorship(User mentor, Long menteeId) {
    User mentee = userRepository.findById(menteeId)
        .orElseThrow(() -> new UsernameNotFoundException("Mentee not found"));

    // Check if the mentee has an approved mentorship request
    MentorshipRequest approvedRequest = mentorshipRequestRepository.findByUserAndStatus(mentee,
            MentorshipRequestStatus.APPROVED)
        .orElseThrow(NoApprovedMentorshipRequestException::new);

    Mentorship mentorship = Mentorship.builder()
        .mentor(mentor)
        .mentee(mentee)
        .startDate(LocalDate.now())
        .status(MentorshipStatus.ACTIVE)
        .build();

    Mentorship savedMentorship = mentorshipRepository.save(mentorship);

    // Update the mentorship request status to indicate it's been fulfilled
    approvedRequest.setStatus(MentorshipRequestStatus.FULFILLED);
    mentorshipRequestRepository.save(approvedRequest);

    return mentorshipMapper.toMentorshipDTO(savedMentorship);
  }

  @Transactional(readOnly = true)
  public PageResponse<MentorshipResponse> getMentorships(MentorshipStatus status, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(SORT_BY_CREATED_AT).descending());
    Page<Mentorship> mentorships;

    if (status == null) {
      mentorships = mentorshipRepository.findAll(pageable);
    } else {
      mentorships = mentorshipRepository.findByStatus(status, pageable);
    }

    List<MentorshipResponse> mentorshipResponses = mentorships.getContent().stream()
        .map(mentorshipMapper::toMentorshipDTO)
        .toList();

    return new PageResponse<>(
        mentorshipResponses,
        mentorships.getNumber(),
        mentorships.getSize(),
        mentorships.getTotalElements(),
        mentorships.getTotalPages(),
        mentorships.isFirst(),
        mentorships.isLast()
    );
  }

  @Transactional
  public MentorshipResponse addNoteToMentorship(Long mentorshipId, String markdownNote) {
    Mentorship mentorship = mentorshipRepository.findById(mentorshipId)
        .orElseThrow(() -> new RuntimeException("Mentorship not found"));

    String currentNotes = mentorship.getNotes();
    String updatedNotes = (currentNotes == null || currentNotes.isEmpty())
        ? markdownNote
        : currentNotes + "\n\n---\n\n" + markdownNote;

    mentorship.setNotes(updatedNotes);

    Mentorship updatedMentorship = mentorshipRepository.save(mentorship);
    return mentorshipMapper.toMentorshipDTO(updatedMentorship);
  }


  @Transactional
  public MentorshipRequestResponse requestMentorship(User user, String reason) {
    MentorshipRequest request = MentorshipRequest.builder()
        .user(user)
        .status(APPROVED)
        .reason(reason)
        .type(MENTEE_REQUEST)
        .build();
    MentorshipRequest savedRequest = mentorshipRequestRepository.save(request);
    return mentorshipMapper.toMentorshipRequestResponse(savedRequest);
  }

  @Transactional(readOnly = true)
  public PageResponse<MentorshipRequestResponse> getPendingRequests(
      MentorshipRequestType requestType, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(SORT_BY_CREATED_AT).descending());

    Page<MentorshipRequest> pendingRequests = mentorshipRequestRepository.findByStatusAndType(
        MentorshipRequestStatus.PENDING,
        requestType,
        pageable
    );

    List<MentorshipRequestResponse> requestResponses = pendingRequests.getContent().stream()
        .map(mentorshipMapper::toMentorshipRequestResponse)
        .toList();

    return new PageResponse<>(
        requestResponses,
        pendingRequests.getNumber(),
        pendingRequests.getSize(),
        pendingRequests.getTotalElements(),
        pendingRequests.getTotalPages(),
        pendingRequests.isFirst(),
        pendingRequests.isLast()
    );
  }

  @Transactional
  public void approveMentorshipRequest(Long requestId) {
    MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
        .orElseThrow(() -> new RuntimeException("Mentorship request not found"));
    Role mentorRole = roleRepository.findByName("MENTOR")
        .orElseThrow(() -> new RuntimeException("Mentor role not found"));

    request.setStatus(MentorshipRequestStatus.APPROVED);
    mentorshipRequestRepository.save(request);

    User user = request.getUser();
    user.getRoles().add(mentorRole);
    userRepository.save(user);
  }

  public void rejectMentorshipRequest(Long requestId) {
    MentorshipRequest request = mentorshipRequestRepository.findById(requestId)
        .orElseThrow(() -> new RuntimeException("Mentorship request not found"));

    request.setStatus(MentorshipRequestStatus.REJECTED);
    mentorshipRequestRepository.save(request);
  }

  @Transactional(readOnly = true)
  public PageResponse<UserResponse> getAvailableMentees(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(SORT_BY_CREATED_AT).descending());

    Page<User> mentees = userRepository.findAvailableMentees(pageable);

    List<UserResponse> userResponses = mentees.getContent().stream()
        .map(userMapper::toUserResponse)
        .toList();

    return new PageResponse<>(
        userResponses,
        mentees.getNumber(),
        mentees.getSize(),
        mentees.getTotalElements(),
        mentees.getTotalPages(),
        mentees.isFirst(),
        mentees.isLast()
    );
  }

  @Transactional
  public void endMentorship(Long mentorshipId, User user, MentorshipFeedback feedback) {
    Mentorship mentorship = mentorshipRepository.findByIdAndEndDateIsNull(mentorshipId)
        .orElseThrow(() -> new RuntimeException("Active mentorship not found"));

    boolean isMentor = mentorship.getMentor().getId().equals(user.getId());
    boolean isMentee = mentorship.getMentee().getId().equals(user.getId());

    if (!isMentor && !isMentee) {
      throw new UserNotPartOfMentorshipException();
    }

    mentorship.setEndDate(LocalDate.now());
    mentorship.setStatus(MentorshipStatus.COMPLETED);

    if (feedback != null) {
      mentorship.setFeedbackRating(feedback.rating());
      mentorship.setFeedbackComments(feedback.comments());
    }

    mentorshipRepository.save(mentorship);
  }
}