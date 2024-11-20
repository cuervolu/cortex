package com.cortex.backend.core.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "course", indexes = {
    @Index(name = "idx_course_name", columnList = "name"),
    @Index(name = "idx_course_slug", columnList = "slug"),
    @Index(name = "idx_course_is_published", columnList = "is_published"),
    @Index(name = "idx_course_display_order", columnList = "display_order"),
    @Index(name = "idx_course_created_at", columnList = "created_at")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Course extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "image_id")
  private Media image;

  @Column(nullable = false)
  private String slug;

  @ManyToMany(mappedBy = "courses")
  private Set<Roadmap> roadmaps;

  @ManyToMany
  @JoinTable(
      name = "course_tags",
      joinColumns = @JoinColumn(name = "course_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id")
  )
  private Set<Tag> tags;
  
  @Column(nullable = false, name = "is_published")
  private Boolean isPublished = false;

  @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ModuleEntity> moduleEntities;

  @Column(name = "display_order", nullable = false)
  private Integer displayOrder = 0;
}