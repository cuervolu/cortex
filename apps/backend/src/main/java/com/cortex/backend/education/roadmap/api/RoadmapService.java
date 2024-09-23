package com.cortex.backend.education.roadmap.api;


import com.cortex.backend.education.roadmap.api.dto.RoadmapRequest;
import com.cortex.backend.education.roadmap.api.dto.RoadmapResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface RoadmapService {
  
  List<RoadmapResponse> getAllRoadmaps();
  
  Optional<RoadmapResponse> getRoadmapBySlug(String slug);

  RoadmapResponse createRoadmap(RoadmapRequest request, MultipartFile image) throws IOException;

  RoadmapResponse updateRoadmap(Long id, RoadmapRequest request, MultipartFile image) throws IOException ;
  
  void deleteRoadmap(Long id);
  

}
