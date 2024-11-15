package com.cortex.backend.education.roadmap.internal;

import com.cortex.backend.core.common.exception.AlreadyEnrolledException;
import com.cortex.backend.core.common.exception.ResourceNotFoundException;
import com.cortex.backend.core.domain.EnrollmentStatus;
import com.cortex.backend.core.domain.EntityType;
import com.cortex.backend.core.domain.RoadmapEnrollment;
import com.cortex.backend.core.domain.RoadmapEnrollmentId;
import com.cortex.backend.education.course.api.CourseService;
import com.cortex.backend.education.course.api.dto.CourseResponse;
import com.cortex.backend.education.progress.api.ProgressTrackingService;
import com.cortex.backend.education.progress.api.RoadmapEnrollmentEvent;
import com.cortex.backend.education.roadmap.api.RoadmapEnrollmentRepository;
import com.cortex.backend.education.roadmap.api.RoadmapService;
import com.cortex.backend.education.roadmap.api.dto.RoadmapEnrollmentResponse;
import com.cortex.backend.education.roadmap.api.dto.RoadmapResponse;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoadmapEnrollmentService {

  private final RoadmapEnrollmentRepository enrollmentRepository;
  private final CourseService courseService;
  private final RoadmapService roadmapService;
  private final ProgressTrackingService progressTrackingService;
  private final ApplicationEventPublisher eventPublisher;
  private final RoadmapEnrollmentMapper enrollmentMapper;

  @Transactional
  public RoadmapEnrollmentResponse enrollUserInRoadmap(Long userId, Long roadmapId) {
    if (enrollmentRepository.isEnrolled(userId, roadmapId)) {
      throw new AlreadyEnrolledException("User already enrolled in this roadmap");
    }

    RoadmapEnrollmentId enrollmentId = new RoadmapEnrollmentId(userId, roadmapId);
    RoadmapEnrollment enrollment = new RoadmapEnrollment(
        enrollmentId,
        LocalDateTime.now(),
        EnrollmentStatus.ACTIVE,
        LocalDateTime.now()
    );

    enrollment = enrollmentRepository.save(enrollment);
    eventPublisher.publishEvent(new RoadmapEnrollmentEvent(userId, roadmapId));

    double progress = calculateRoadmapProgress(userId, roadmapId);
    return enrollmentMapper.toResponse(enrollment, progress);
  }

  public List<RoadmapEnrollmentResponse> getUserEnrollments(Long userId) {
    return enrollmentRepository.findByIdUserId(userId).stream()
        .map(enrollment -> {
          double progress = calculateRoadmapProgress(userId, enrollment.getId().getRoadmapId());
          return enrollmentMapper.toResponse(enrollment, progress);
        })
        .toList();
  }

  public double calculateRoadmapProgress(Long userId, Long roadmapId) {
    RoadmapResponse roadmap = roadmapService.getRoadmapById(roadmapId)
        .orElseThrow(() -> new ResourceNotFoundException("Roadmap not found"));

    long totalCourses = roadmap.getCourseSlugs().size();
    if (totalCourses == 0) {
      return 0.0;
    }

    long completedCourses = roadmap.getCourseSlugs().stream()
        .map(slug -> courseService.getCourseBySlug(slug)
            .map(CourseResponse::getId)
            .orElse(null))
        .filter(courseId -> courseId != null &&
            progressTrackingService.isEntityCompleted(userId, courseId, EntityType.COURSE))
        .count();

    return (double) completedCourses / totalCourses * 100;
  }
}