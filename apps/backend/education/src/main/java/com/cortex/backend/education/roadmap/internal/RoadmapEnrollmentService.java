package com.cortex.backend.education.roadmap.internal;

import com.cortex.backend.core.common.exception.AlreadyEnrolledException;
import com.cortex.backend.core.common.exception.ResourceNotFoundException;
import com.cortex.backend.core.domain.Course;
import com.cortex.backend.core.domain.EnrollmentStatus;
import com.cortex.backend.core.domain.EntityType;
import com.cortex.backend.core.domain.Exercise;
import com.cortex.backend.core.domain.Lesson;
import com.cortex.backend.core.domain.ModuleEntity;
import com.cortex.backend.core.domain.RoadmapEnrollment;
import com.cortex.backend.core.domain.RoadmapEnrollmentId;
import com.cortex.backend.education.course.api.CourseRepository;
import com.cortex.backend.education.course.api.CourseService;
import com.cortex.backend.education.course.api.dto.CourseResponse;
import com.cortex.backend.education.progress.api.ProgressTrackingService;
import com.cortex.backend.education.progress.api.ProgressUpdatedEvent;
import com.cortex.backend.education.progress.api.RoadmapEnrollmentEvent;
import com.cortex.backend.education.progress.api.UserProgressService;
import com.cortex.backend.education.progress.internal.EntityRelationService;
import com.cortex.backend.education.roadmap.api.RoadmapEnrollmentRepository;
import com.cortex.backend.education.roadmap.api.RoadmapService;
import com.cortex.backend.education.roadmap.api.dto.RoadmapEnrollmentResponse;
import com.cortex.backend.education.roadmap.api.dto.RoadmapResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoadmapEnrollmentService {

  private final RoadmapEnrollmentRepository enrollmentRepository;
  private final CourseService courseService;
  private final CourseRepository courseRepository;
  private final RoadmapService roadmapService;
  private final ProgressTrackingService progressTrackingService;
  private final TransactionTemplate transactionTemplate;
  private final UserProgressService userProgressService;
  private final ApplicationEventPublisher eventPublisher;
  private final RoadmapEnrollmentMapper enrollmentMapper;
  private final EntityRelationService entityRelationService;

  private static final String PROGRESS_CACHE = "roadmap-progress";

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

    double progress = getProgress(userId, roadmapId);
    return enrollmentMapper.toResponse(enrollment, progress);
  }

  public List<RoadmapEnrollmentResponse> getUserEnrollments(Long userId) {
    return enrollmentRepository.findByIdUserId(userId).stream()
        .map(enrollment -> {
          double progress = calculateRoadmapProgress(
              userId,
              enrollment.getId().getRoadmapId()
          );
          return enrollmentMapper.toResponse(enrollment, progress);
        })
        .toList();
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  protected void updateEnrollmentStatusInternal(Long userId, Long roadmapId,
      EnrollmentStatus newStatus) {
    RoadmapEnrollmentId enrollmentId = new RoadmapEnrollmentId(userId, roadmapId);
    enrollmentRepository.findById(enrollmentId)
        .ifPresent(enrollment -> {
          enrollment.setStatus(newStatus);
          enrollment.setLastActivityDate(LocalDateTime.now());
          enrollmentRepository.save(enrollment);
        });
  }

  public RoadmapEnrollmentResponse updateEnrollmentStatus(Long userId, Long roadmapId,
      EnrollmentStatus newStatus) {
    updateEnrollmentStatusInternal(userId, roadmapId, newStatus);
    double progress = getProgress(userId, roadmapId);
    return enrollmentMapper.toResponse(
        enrollmentRepository.findById(new RoadmapEnrollmentId(userId, roadmapId))
            .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found")),
        progress
    );
  }

  @Transactional
  public void updateLastActivityDate(Long userId, Long roadmapId) {
    RoadmapEnrollmentId enrollmentId = new RoadmapEnrollmentId(userId, roadmapId);
    enrollmentRepository.findById(enrollmentId)
        .ifPresent(enrollment -> {
          enrollment.setLastActivityDate(LocalDateTime.now());
          enrollmentRepository.save(enrollment);
        });
  }

  @EventListener
  public void handleEnrollmentUpdate(ProgressUpdatedEvent event) {
    Long roadmapId = entityRelationService.findRelatedRoadmapId(
        event.entityId(),
        event.entityType()
    );

    if (roadmapId != null) {
      transactionTemplate.execute(status -> {
        try {
          updateLastActivityDate(event.userId(), roadmapId);
          double progress = getProgress(event.userId(), roadmapId);
          if (progress >= 100.0) {
            updateEnrollmentStatus(event.userId(), roadmapId, EnrollmentStatus.COMPLETED);
          }
          return null;
        } catch (Exception e) {
          status.setRollbackOnly();
          log.error("Error handling enrollment update", e);
          return null;
        }
      });
    }
  }

  private double getProgress(Long userId, Long roadmapId) {
    return calculateRoadmapProgress(userId, roadmapId);
  }

  
  public double calculateRoadmapProgress(Long userId, Long roadmapId) {
    Double result = transactionTemplate.execute(_ -> {
      try {
        RoadmapResponse roadmap = roadmapService.getRoadmapById(roadmapId)
            .orElseThrow(() -> new ResourceNotFoundException("Roadmap not found"));

        if (roadmap.getCourseSlugs().isEmpty()) {
          return 0.0;
        }

        double totalCredits = 0.0;
        double earnedCredits = 0.0;

        for (String courseSlug : roadmap.getCourseSlugs()) {
          Optional<CourseResponse> courseOpt = courseService.getCourseBySlug(courseSlug);
          if (courseOpt.isPresent()) {
            CourseCredits courseCredits = calculateCourseCredits(userId, courseOpt.get().getId());
            totalCredits += courseCredits.total();
            earnedCredits += courseCredits.earned();
          }
        }

        return totalCredits > 0 ? (earnedCredits / totalCredits) * 100 : 0.0;
      } catch (Exception e) {
        log.error("Error calculating roadmap progress for user {} and roadmap {}",
            userId, roadmapId, e);
        return 0.0;
      }
    });
    return result != null ? result : 0.0;
  }

  @CacheEvict(value = PROGRESS_CACHE, key = "{#userId, #roadmapId}")
  public void evictProgressCache(Long userId, Long roadmapId) {
    log.debug("Evicted progress cache for user {} and roadmap {}", userId, roadmapId);
  }

  private record CourseCredits(double earned, double total) {

  }

  private CourseCredits calculateCourseCredits(Long userId, Long courseId) {
    double totalCredits = 0.0;
    double earnedCredits = 0.0;

    Course course = courseRepository.findByIdWithDetails(courseId)
        .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

    for (ModuleEntity module : course.getModuleEntities()) {
      for (Lesson lesson : module.getLessons()) {
        double lessonCredits = lesson.getCredits();
        totalCredits += lessonCredits;

        if (userProgressService.isEntityCompleted(userId, lesson.getId(), EntityType.LESSON)) {
          earnedCredits += lessonCredits;
        }

        for (Exercise exercise : lesson.getExercises()) {
          double exercisePoints = exercise.getPoints();
          totalCredits += exercisePoints;

          if (userProgressService.isEntityCompleted(userId, exercise.getId(),
              EntityType.EXERCISE)) {
            earnedCredits += exercisePoints;
          }
        }
      }
    }

    return new CourseCredits(earnedCredits, totalCredits);
  }

  @EventListener
  public void handleProgressUpdated(ProgressUpdatedEvent event) {
    Long roadmapId = entityRelationService.findRelatedRoadmapId(event.entityId(),
        event.entityType());
    if (roadmapId != null) {
      evictProgressCache(event.userId(), roadmapId);
    }
  }
}