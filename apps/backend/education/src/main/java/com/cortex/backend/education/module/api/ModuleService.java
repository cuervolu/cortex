package com.cortex.backend.education.module.api;

import com.cortex.backend.education.module.api.dto.ModuleRequest;
import com.cortex.backend.education.module.api.dto.ModuleResponse;
import com.cortex.backend.education.module.api.dto.ModuleUpdateRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface ModuleService {

  List<ModuleResponse> getAllModules();

  Optional<ModuleResponse> getModuleById(Long id);

  Optional<ModuleResponse> getModuleBySlug(String slug);

  ModuleResponse createModule(ModuleRequest request);
  ModuleResponse updateModule(Long id, ModuleUpdateRequest request);

  void deleteModule(Long id);
  ModuleResponse uploadModuleImage(Long id, MultipartFile image, String altText) throws IOException;
}
