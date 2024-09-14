package com.cortex.backend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

  @Resource
  private final Cloudinary cloudinary;

  public Map uploadImage(MultipartFile file, String folder) throws IOException {
    Map<String, Object> params = ObjectUtils.asMap(
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

}