package com.cortex.backend.common;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.cortex.backend.common.exception.FileSizeExceededException;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageUtils {

  private final Cloudinary cloudinary;

  @Value("${max.file.upload.size:5242880}") // 5MB default
  private long maxFileUploadSize;

  private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
      "image/jpeg", "image/png", "image/webp"
  );

  public Map<String, Object> uploadImage(MultipartFile file, String folder) throws IOException {
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

  public static MultipartFile downloadImageFromUrl(String imageUrl) throws IOException, URISyntaxException {
    URI uri = new URI(imageUrl);
    URL url = uri.toURL();
    URLConnection connection = url.openConnection();
    connection.setConnectTimeout(5000);
    connection.setReadTimeout(5000);
    try (InputStream inputStream = connection.getInputStream()) {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte[] buffer = new byte[4096];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
      byte[] imageBytes = outputStream.toByteArray();
      String fileExtension = getFileExtension(imageUrl);
      String contentType = getContentType(fileExtension);
      return new ByteArrayMultipartFile(
          "file",
          "avatar." + fileExtension,
          contentType,
          imageBytes
      );
    }
  }

  private static String getFileExtension(String url) {
    String extension = url.substring(url.lastIndexOf(".") + 1);
    return extension.length() > 4 ? "jpg" : extension; // default to jpg if extension is too long
  }

  private static String getContentType(String fileExtension) {
    return switch (fileExtension.toLowerCase()) {
      case "png" -> "image/png";
      case "gif" -> "image/gif";
      case "webp" -> "image/webp";
      default -> "image/jpeg";
    };
  }
}