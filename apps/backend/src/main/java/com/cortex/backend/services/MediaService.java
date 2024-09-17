package com.cortex.backend.services;

import com.cortex.backend.entities.Media;
import com.cortex.backend.repositories.MediaRepository;
import com.cortex.backend.common.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MediaService {

  private final MediaRepository mediaRepository;
  private final ImageUtils imageUtils;

  @Transactional(readOnly = true)
  public List<Media> getAllMedia() {
    return (List<Media>) mediaRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Optional<Media> getMediaById(Long id) {
    return mediaRepository.findById(id);
  }

  @Transactional
  public Media uploadMedia(MultipartFile file, String altText, String folder, String subfolder)
      throws IOException {
    imageUtils.validateFileSize(file);
    if (!imageUtils.isValidImageFile(file)) {
      throw new IllegalArgumentException(
          "Invalid file type. Only JPEG, PNG, and WebP are allowed.");
    }

    String uploadFolder = folder;
    if (subfolder != null && !subfolder.isEmpty()) {
      uploadFolder += "/" + subfolder;
    }

    Map<String, Object> uploadResult = imageUtils.uploadImage(file, uploadFolder);
    Media media = Media.builder()
        .name(file.getOriginalFilename())
        .url((String) uploadResult.get("url"))
        .mimeType(file.getContentType())
        .size(file.getSize())
        .altText(altText)
        .cloudinaryPublicId((String) uploadResult.get("public_id"))
        .build();
    return mediaRepository.save(media);
  }

  @Transactional
  public Media updateMedia(Long id, String newAltText) {
    Media media = mediaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Media not found"));
    media.setAltText(newAltText);
    return mediaRepository.save(media);
  }

  @Transactional
  public void deleteMedia(Long id) {
    mediaRepository.deleteById(id);
  }

  public String createResizedImageUrl(String publicId, int width, int height) {
    return imageUtils.createUrl(publicId, width, height);
  }
}