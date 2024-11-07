package com.cortex.backend.education.module.api;

import com.cortex.backend.core.common.PageResponse;
import com.cortex.backend.education.module.api.dto.ModuleRequest;
import com.cortex.backend.education.module.api.dto.ModuleResponse;
import com.cortex.backend.education.module.api.dto.ModuleUpdateRequest;
import java.io.IOException;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface ModuleService {

  PageResponse<ModuleResponse> getAllModules(int page, int size);

  Optional<ModuleResponse> getModuleById(Long id);

  Optional<ModuleResponse> getModuleBySlug(String slug);

  ModuleResponse createModule(ModuleRequest request);

  ModuleResponse updateModule(Long id, ModuleUpdateRequest request);

  void deleteModule(Long id);

  ModuleResponse uploadModuleImage(Long id, MultipartFile image, String altText) throws IOException;

  Long getCourseIdForModule(Long moduleId);

  boolean areAllLessonsCompleted(Long userId, Long moduleId);
}
