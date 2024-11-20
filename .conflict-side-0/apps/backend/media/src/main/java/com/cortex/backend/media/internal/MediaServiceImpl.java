package com.cortex.backend.media.internal;

import com.cortex.backend.media.api.ImageUtils;
import com.cortex.backend.media.api.MediaService;
import com.cortex.backend.core.domain.Media;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {
  private final MediaRepository mediaRepository;
  private final ImageUtils imageUtils;

  @Override
  @Transactional(readOnly = true)
  public List<Media> getAllMedia() {
    return (List<Media>) mediaRepository.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Media> getMediaById(Long id) {
    return mediaRepository.findById(id);
  }

  @Override
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

  @Override
  @Transactional
  public Media updateMedia(Long id, String newAltText) {
    Media media = mediaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Media not found"));
    media.setAltText(newAltText);
    return mediaRepository.save(media);
  }

  @Override
  @Transactional
  public void deleteMedia(Long id) {
    mediaRepository.deleteById(id);
  }

  @Override
  public String createResizedImageUrl(String publicId, int width, int height) {
    return imageUtils.createUrl(publicId, width, height);
  }
}
