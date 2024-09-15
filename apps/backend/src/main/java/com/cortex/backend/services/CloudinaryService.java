package com.cortex.backend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.cortex.backend.exception.FileSizeExceededException;
import jakarta.annotation.Resource;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

  @Resource
  private final Cloudinary cloudinary;

  @Value("${max.file.upload.size:5242880}") // 5MB default
  private long maxFileUploadSize;

  private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
      "image/jpeg", "image/png", "image/webp"
  );


  public Map uploadImage(MultipartFile file, String folder) throws IOException {
    var params = ObjectUtils.asMap(
        "use_filename", true,
        "unique_filename", false,
        "overwrite", true,
        "folder", folder
    );
    return cloudinary.uploader().upload(file.getBytes(), params);
  }

  public String createUrl(String publicId, int width, int height) {
    return cloudinary.url()
        .transformation(new Transformation<>().crop("fill").width(width).height(height))
        .generate(publicId);
  }

  public boolean isValidImageFile(MultipartFile file) {
    try {
      String contentType = file.getContentType();
      return contentType != null && ALLOWED_CONTENT_TYPES.contains(contentType);
    } catch (Exception e) {
      log.error("Error while checking if file is an image", e);
      return false;
    }
  }
  
  public void validateFileSize(MultipartFile file) {
    if (file.getSize() > maxFileUploadSize) {
      throw new FileSizeExceededException("File size exceeds maximum limit");
    }
  }


}