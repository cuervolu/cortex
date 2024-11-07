package com.cortex.backend.education.mentorship.api;

import com.cortex.backend.core.common.PageResponse;
import com.cortex.backend.core.domain.MentorshipRequest;
import com.cortex.backend.core.domain.MentorshipRequestType;
import com.cortex.backend.core.domain.MentorshipStatus;
import com.cortex.backend.core.domain.User;
import com.cortex.backend.education.mentorship.api.dto.MenteeRequest;
import com.cortex.backend.education.mentorship.api.dto.MentorApplicationRequest;
import com.cortex.backend.education.mentorship.api.dto.MentorResponse;
import com.cortex.backend.education.mentorship.api.dto.MentorshipCreation;
import com.cortex.backend.education.mentorship.api.dto.MentorshipFeedback;
import com.cortex.backend.education.mentorship.api.dto.MentorshipNote;
import com.cortex.backend.education.mentorship.api.dto.MentorshipRequestResponse;
import com.cortex.backend.education.mentorship.api.dto.MentorshipResponse;
import com.cortex.backend.education.mentorship.internal.MentorshipService;
import com.cortex.backend.user.api.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
@Tag(name = "Mentorship", description = "Endpoints for managing mentorship requests and mentees")
public class MentorshipController {

  private final MentorshipService mentorshipService;

  @PostMapping("/apply")
  @Operation(summary = "Apply to become a mentor", description = "Allows a user to apply to become a mentor")
  @ApiResponse(responseCode = "200", description = "Mentor application submitted successfully",
      content = @Content(schema = @Schema(implementation = MentorshipRequest.class)))
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<MentorshipRequestResponse> applyToBeMentor(
      @Parameter(description = "Authenticated user", hidden = true)
      @AuthenticationPrincipal User user,
      @Parameter(description = "Mentor application details")
      @RequestBody @Valid MentorApplicationRequest application) {
    return ResponseEntity.ok(mentorshipService.applyToBeMentor(user, application));
  }

  @GetMapping("/mentors")
  @Operation(summary = "Get available mentors", description = "Retrieves a paginated list of all available mentors")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = PageResponse.class)))
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<PageResponse<MentorResponse>> getAvailableMentors(
      @Parameter(description = "Page number (0-indexed)")
      @RequestParam(name = "page", defaultValue = "0") int page,
      @Parameter(description = "Number of items per page")
      @RequestParam(name = "size", defaultValue = "10") int size
  ) {
    return ResponseEntity.ok(mentorshipService.getAvailableMentors(page, size));
  }

  @PostMapping("/create")
  @Operation(summary = "Create a new mentorship", description = "Creates a new mentorship between a mentor and a mentee")
  @ApiResponse(responseCode = "200", description = "Mentorship created successfully",
      content = @Content(schema = @Schema(implementation = MentorshipResponse.class)))
  @PreAuthorize("hasRole('MENTOR')")
  public ResponseEntity<MentorshipResponse> createMentorship(
      @Parameter(description = "Authenticated user (mentor)", hidden = true)
      @AuthenticationPrincipal User mentor,
      @Parameter(description = "Mentorship creation details")
      @RequestBody @Valid MentorshipCreation creationDTO) {
    return ResponseEntity.ok(mentorshipService.createMentorship(mentor, creationDTO.menteeId()));
  }

  @PostMapping("/request-mentee")
  @Operation(summary = "Request mentorship", description = "Allows a user to request mentorship as a mentee")
  @ApiResponse(responseCode = "200", description = "Mentee request created successfully",
      content = @Content(schema = @Schema(implementation = MentorshipRequestResponse.class)))
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<MentorshipRequestResponse> requestMentorship(
      @Parameter(description = "Authenticated user", hidden = true)
      @AuthenticationPrincipal User user,
      @Parameter(description = "Mentee request details")
      @RequestBody @Valid MenteeRequest request) {
    return ResponseEntity.ok(mentorshipService.requestMentorship(user, request.reason()));
  }


  @GetMapping("/pending")
  @Operation(summary = "Get pending mentorship requests",
      description = "Retrieves all pending mentorship requests of a specific type (MENTEE_REQUEST by default)")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = PageResponse.class)))
  @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
  public ResponseEntity<PageResponse<MentorshipRequestResponse>> getPendingRequests(
      @Parameter(description = "Type of mentorship request (MENTEE_REQUEST or MENTOR_APPLICATION)")
      @RequestParam(name = "request-type", defaultValue = "MENTEE_REQUEST") MentorshipRequestType requestType,
      @Parameter(description = "Page number (0-indexed)")
      @RequestParam(name = "page", defaultValue = "0") int page,
      @Parameter(description = "Number of items per page")
      @RequestParam(name = "size", defaultValue = "10") int size
  ) {
    return ResponseEntity.ok(mentorshipService.getPendingRequests(requestType, page, size));
  }
  
  @PostMapping("/approve/{requestId}")
  @Operation(summary = "Approve mentorship request", description = "Approves a specific mentorship request")
  @ApiResponse(responseCode = "200", description = "Mentorship request approved successfully")
  @ApiResponse(responseCode = "404", description = "Mentorship request not found")
  @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
  public ResponseEntity<Void> approveMentorshipRequest(
      @Parameter(description = "ID of the mentorship request to approve")
      @PathVariable Long requestId) {
    mentorshipService.approveMentorshipRequest(requestId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/reject/{requestId}")
  @Operation(summary = "Reject mentorship request", description = "Rejects a specific mentorship request")
  @ApiResponse(responseCode = "200", description = "Mentorship request rejected successfully")
  @ApiResponse(responseCode = "404", description = "Mentorship request not found")
  @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
  public ResponseEntity<Void> rejectMentorshipRequest(
      @Parameter(description = "ID of the mentorship request to reject")
      @PathVariable Long requestId) {
    mentorshipService.rejectMentorshipRequest(requestId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/available-mentees")
  @Operation(summary = "Get available mentees", description = "Retrieves a paginated list of all available mentees")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = PageResponse.class)))
  @PreAuthorize("hasRole('MENTOR')")
  public ResponseEntity<PageResponse<UserResponse>> getAvailableMentees(
      @Parameter(description = "Page number (0-indexed)")
      @RequestParam(name = "page", defaultValue = "0") int page,
      @Parameter(description = "Number of items per page")
      @RequestParam(name = "size", defaultValue = "10") int size
  ) {
    return ResponseEntity.ok(mentorshipService.getAvailableMentees(page, size));
  }

  @GetMapping
  @Operation(summary = "Get mentorships", description = "Retrieves a paginated list of mentorships, optionally filtered by status")
  @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = PageResponse.class)))
  @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'MENTOR')")
  public ResponseEntity<PageResponse<MentorshipResponse>> getMentorships(
      @Parameter(description = "Status of mentorships to retrieve (optional)")
      @RequestParam(required = false) MentorshipStatus status,
      @Parameter(description = "Page number (0-indexed)")
      @RequestParam(name = "page", defaultValue = "0") int page,
      @Parameter(description = "Number of items per page")
      @RequestParam(name = "size", defaultValue = "10") int size
  ) {
    return ResponseEntity.ok(mentorshipService.getMentorships(status, page, size));
  }

  @PostMapping("/{mentorshipId}/end")
  @Operation(summary = "End a mentorship", description = "Ends an active mentorship and optionally provides feedback")
  @ApiResponse(responseCode = "200", description = "Mentorship ended successfully")
  @ApiResponse(responseCode = "404", description = "Active mentorship not found")
  @ApiResponse(responseCode = "403", description = "User is not part of this mentorship")
  @PreAuthorize("hasAnyRole('MENTOR', 'USER')")
  public ResponseEntity<Void> endMentorship(
      @Parameter(description = "ID of the mentorship to end")
      @PathVariable Long mentorshipId,
      @Parameter(description = "Authenticated user", hidden = true)
      @AuthenticationPrincipal User user,
      @Parameter(description = "Feedback for the mentorship (optional)")
      @RequestBody(required = false) @Valid MentorshipFeedback feedback) {
    mentorshipService.endMentorship(mentorshipId, user, feedback);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{mentorshipId}/note")
  @Operation(summary = "Add a note to a mentorship",
      description = "Adds a Markdown formatted note to an existing mentorship")
  @ApiResponse(responseCode = "200", description = "Note added successfully",
      content = @Content(schema = @Schema(implementation = MentorshipResponse.class)))
  @PreAuthorize("hasAnyRole('MENTOR', 'USER')")
  public ResponseEntity<MentorshipResponse> addNoteToMentorship(
      @Parameter(description = "ID of the mentorship")
      @PathVariable Long mentorshipId,
      @Parameter(description = "Markdown formatted note")
      @RequestBody @Valid MentorshipNote noteDTO) {
    return ResponseEntity.ok(
        mentorshipService.addNoteToMentorship(mentorshipId, noteDTO.content()));
  }
}