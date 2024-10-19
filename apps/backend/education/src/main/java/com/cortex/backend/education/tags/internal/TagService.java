package com.cortex.backend.education.tags.internal;

import com.cortex.backend.core.domain.Tag;
import com.cortex.backend.education.tags.api.TagRepository;
import com.cortex.backend.education.tags.api.dto.TagDTO;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TagService {
  private final TagRepository tagRepository;

  @Transactional
  public Set<Tag> getOrCreateTags(Set<TagDTO> tagDTOs) {
    return tagDTOs.stream()
        .map(this::getOrCreateTag)
        .collect(Collectors.toSet());
  }

  private Tag getOrCreateTag(TagDTO tagDTO) {
    return tagRepository.findByName(tagDTO.getName())
        .orElseGet(() -> tagRepository.save(new Tag(null, tagDTO.getName())));
  }

}
