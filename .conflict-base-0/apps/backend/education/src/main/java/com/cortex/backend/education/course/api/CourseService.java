package com.cortex.backend.education.course.api;

import com.cortex.backend.core.common.PageResponse;
import com.cortex.backend.education.course.api.dto.CourseRequest;
import com.cortex.backend.education.course.api.dto.CourseResponse;
import com.cortex.backend.education.course.api.dto.CourseUpdateRequest;
import com.cortex.backend.education.module.api.dto.ModuleResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface CourseService {
  
  PageResponse<CourseResponse> getAllCourses(int page, int size);
  
  Optional<CourseResponse> getCourseById(Long id);
  
  Optional<CourseResponse> getCourseBySlug(String slug);

  CourseResponse createCourse(CourseRequest request);

  CourseResponse updateCourse(Long id, CourseUpdateRequest request);

  Optional<PageResponse<ModuleResponse>> getModulesForCourse(String courseSlug, int page, int size) ;
  Optional<ModuleResponse> getModuleForCourse(String courseSlug, String moduleSlug);
  
  void deleteCourse(Long id);
  
  CourseResponse uploadCourseImage(Long id, MultipartFile image, String altText) throws IOException;

  Long getRoadmapIdForCourse(Long courseId);
  boolean areAllModulesCompleted(Long userId, Long courseId);
}
