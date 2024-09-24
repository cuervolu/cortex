package com.cortex.backend.education.roadmap.internal;

import com.cortex.backend.common.SlugUtils;
import com.cortex.backend.education.course.domain.Course;
import com.cortex.backend.education.course.internal.CourseRepository;
import com.cortex.backend.education.domain.Tag;
import com.cortex.backend.education.internal.TagRepository;
import com.cortex.backend.education.roadmap.api.RoadmapService;
import com.cortex.backend.education.roadmap.api.dto.RoadmapRequest;
import com.cortex.backend.education.roadmap.api.dto.RoadmapResponse;
import com.cortex.backend.education.roadmap.api.dto.RoadmapUpdateRequest;
import com.cortex.backend.education.roadmap.domain.Roadmap;
import com.cortex.backend.media.api.MediaService;
import com.cortex.backend.media.domain.Media;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoadmapServiceImpl implements RoadmapService {

  private final RoadmapRepository roadmapRepository;
  private final RoadmapMapper roadmapMapper;
  private final MediaService mediaService;
  private final SlugUtils slugUtils;
  private final TagRepository tagRepository;
  private final CourseRepository courseRepository;
  
  private static final String ROADMAP_IMAGE_UPLOAD_PATH = "roadmaps";
  private static final String ROADMAP_NOT_FOUND_MESSAGE = "Roadmap not found";

  @Override
  @Transactional(readOnly = true)
  public List<RoadmapResponse> getAllRoadmaps() {
    return StreamSupport.stream(roadmapRepository.findAll().spliterator(), false)
        .map(roadmapMapper::toRoadmapResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<RoadmapResponse> getRoadmapBySlug(String slug) {
    return roadmapRepository.findBySlug(slug)
        .map(roadmapMapper::toRoadmapResponse);
  }

  @Override
  @Transactional
  public RoadmapResponse createRoadmap(RoadmapRequest request) {
    Roadmap roadmap = roadmapMapper.toRoadmap(request);
    roadmap.setSlug(generateUniqueSlug(request.getTitle()));
    setRoadmapRelations(roadmap, request);
    Roadmap savedRoadmap = roadmapRepository.save(roadmap);
    return roadmapMapper.toRoadmapResponse(savedRoadmap);
  }

  @Override
  @Transactional
  public RoadmapResponse updateRoadmap(Long id, RoadmapUpdateRequest request) {
    Roadmap existingRoadmap = roadmapRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(ROADMAP_NOT_FOUND_MESSAGE));

    if (request.getTitle() != null) {
      existingRoadmap.setTitle(request.getTitle());
      if (!existingRoadmap.getTitle().equals(request.getTitle())) {
        existingRoadmap.setSlug(generateUniqueSlug(request.getTitle(), existingRoadmap.getSlug()));
      }
    }
    if (request.getDescription() != null) {
      existingRoadmap.setDescription(request.getDescription());
    }

    setRoadmapRelations(existingRoadmap, request);
    Roadmap updatedRoadmap = roadmapRepository.save(existingRoadmap);
    return roadmapMapper.toRoadmapResponse(updatedRoadmap);
  }

  @Override
  @Transactional
  public void deleteRoadmap(Long id) {
    Roadmap roadmap = roadmapRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(ROADMAP_NOT_FOUND_MESSAGE));
    roadmapRepository.delete(roadmap);
  }

  private String generateUniqueSlug(String title) {
    return slugUtils.generateUniqueSlug(title, roadmapRepository::existsBySlug);
  }

  private String generateUniqueSlug(String title, String currentSlug) {
    return slugUtils.generateUniqueSlug(title,
        slug -> !slug.equals(currentSlug) && roadmapRepository.existsBySlug(slug));
  }

  private void setRoadmapRelations(Roadmap roadmap, RoadmapRequest request) {
    setRoadmapTags(roadmap, request.getTagIds());
    setRoadmapCourses(roadmap, request.getCourseIds());
  }

  private void setRoadmapRelations(Roadmap roadmap, RoadmapUpdateRequest request) {
    if (request.getTagIds() != null) {
      setRoadmapTags(roadmap, request.getTagIds());
    }
    if (request.getCourseIds() != null) {
      setRoadmapCourses(roadmap, request.getCourseIds());
    }
  }

  private void setRoadmapTags(Roadmap roadmap, Set<Long> tagIds) {
    if (tagIds != null) {
      Set<Tag> tags = StreamSupport.stream(tagRepository.findAllById(tagIds).spliterator(), false)
          .collect(Collectors.toSet());
      roadmap.setTags(tags);
    }
  }

  private void setRoadmapCourses(Roadmap roadmap, Set<Long> courseIds) {
    if (courseIds != null) {
      Set<Course> courses = StreamSupport.stream(
              courseRepository.findAllById(courseIds).spliterator(), false)
          .collect(Collectors.toSet());
      roadmap.setCourses(courses);
    }
  }

  private void handleImageUpload(Roadmap roadmap, MultipartFile image, String altText)
      throws IOException {
    if (image != null && !image.isEmpty()) {
      Media uploadedMedia = mediaService.uploadMedia(image, altText, ROADMAP_IMAGE_UPLOAD_PATH, roadmap.getSlug());
      roadmap.setImage(uploadedMedia);
    }
  }

  @Override
  @Transactional
  public RoadmapResponse uploadRoadmapImage(Long id, MultipartFile image, String altText) throws IOException {
    Roadmap roadmap = roadmapRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(ROADMAP_NOT_FOUND_MESSAGE));

    handleImageUpload(roadmap, image, altText);
    Roadmap updatedRoadmap = roadmapRepository.save(roadmap);
    return roadmapMapper.toRoadmapResponse(updatedRoadmap);
  }
}