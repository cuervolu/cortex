package com.cortex.backend.media.domain;

import com.cortex.backend.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "media", indexes = {
    @Index(name = "idx_media_name", columnList = "name"),
    @Index(name = "idx_media_cloudinary_public_id", columnList = "cloudinary_public_id")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Media extends BaseEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String url;

  @Column(name = "mime_type")
  private String mimeType;

  private Long size;

  @Column(name = "alt_text")
  private String altText;

  @Column(name = "cloudinary_public_id")
  private String cloudinaryPublicId;
}