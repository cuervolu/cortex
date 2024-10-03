package com.cortex.backend.education.roadmap.api;


import com.cortex.backend.education.roadmap.api.dto.RoadmapDetails;
import com.cortex.backend.education.roadmap.api.dto.RoadmapRequest;
import com.cortex.backend.education.roadmap.api.dto.RoadmapResponse;
import com.cortex.backend.education.roadmap.api.dto.RoadmapUpdateRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface RoadmapService {

  List<RoadmapResponse> getAllRoadmaps();

  Optional<RoadmapDetails> getRoadmapBySlug(String slug);

  RoadmapResponse createRoadmap(RoadmapRequest request);

  RoadmapResponse updateRoadmap(Long id, RoadmapUpdateRequest request);

  void deleteRoadmap(Long id);

  RoadmapResponse uploadRoadmapImage(Long id, MultipartFile image, String altText)
      throws IOException;

  boolean areAllCoursesCompleted(Long userId, Long roadmapId);

}
