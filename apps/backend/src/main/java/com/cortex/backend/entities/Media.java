package com.cortex.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "media")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Media extends BaseEntity {

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