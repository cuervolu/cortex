package com.cortex.backend.education.course.api;

import com.cortex.backend.education.course.api.dto.CourseRequest;
import com.cortex.backend.education.course.api.dto.CourseResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface CourseService {
  
  List<CourseResponse> getAllCourses();
  
  Optional<CourseResponse> getCourseById(Long id);
  
  Optional<CourseResponse> getCourseBySlug(String slug);

  CourseResponse createCourse(CourseRequest request, MultipartFile image) throws IOException;

  CourseResponse updateCourse(Long id, CourseRequest request, MultipartFile image) throws IOException;
  
  void deleteCourse(Long id);

}
