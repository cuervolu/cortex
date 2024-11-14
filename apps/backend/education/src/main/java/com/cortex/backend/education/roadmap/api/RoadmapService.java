package com.cortex.backend.education.roadmap.api;


import com.cortex.backend.core.common.PageResponse;
import com.cortex.backend.core.domain.User;
import com.cortex.backend.education.course.api.dto.CourseResponse;
import com.cortex.backend.education.progress.api.ProgressUpdatedEvent;
import com.cortex.backend.education.roadmap.api.dto.RoadmapDetails;
import com.cortex.backend.education.roadmap.api.dto.RoadmapRequest;
import com.cortex.backend.education.roadmap.api.dto.RoadmapResponse;
import com.cortex.backend.education.roadmap.api.dto.RoadmapUpdateRequest;
import java.io.IOException;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface RoadmapService {

  PageResponse<RoadmapResponse> getAllPublishedRoadmaps(int page, int size, String[] sort);

  PageResponse<RoadmapResponse> getAllRoadmaps(int page, int size, String[] sort) ;

  Optional<RoadmapDetails> getRoadmapBySlug(String slug, User user);

  RoadmapResponse createRoadmap(RoadmapRequest request);

  RoadmapResponse updateRoadmap(Long id, RoadmapUpdateRequest request);

  Optional<CourseResponse> getCourseFromRoadmap(String roadmapSlug, String courseSlug);

  void deleteRoadmap(Long id);

  RoadmapResponse uploadRoadmapImage(Long id, MultipartFile image, String altText)
      throws IOException;

  boolean areAllCoursesCompleted(Long userId, Long roadmapId);

  void handleProgressUpdated(ProgressUpdatedEvent event);

  Optional<PageResponse<CourseResponse>> getAvailableCourses(Long roadmapId, int page, int size, String[] sort, boolean includeUnpublished);

  Optional<PageResponse<CourseResponse>> getRoadmapCourses(Long roadmapId, int page, int size, String[] sort);

}
