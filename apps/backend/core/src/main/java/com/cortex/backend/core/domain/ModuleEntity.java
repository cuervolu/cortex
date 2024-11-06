package com.cortex.backend.core.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
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
@Table(name = "module", indexes = {
    @Index(name = "idx_module_name", columnList = "name"),
    @Index(name = "idx_module_slug", columnList = "slug"),
    @Index(name = "idx_module_is_published", columnList = "is_published"),
    @Index(name = "idx_module_course", columnList = "course_id"),
    @Index(name = "idx_module_course_order", columnList = "course_id,display_order"),
    @Index(name = "idx_module_created_at", columnList = "created_at")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleEntity extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "course_id", nullable = false)
  private Course course;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;
  
  @Column(name = "is_published", nullable = false)
  private Boolean isPublished = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "image_id")
  private Media image;

  @Column(nullable = false)
  private String slug;

  @OneToMany(mappedBy = "moduleEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Lesson> lessons;

  @Column(name = "display_order", nullable = false)
  private Integer displayOrder = 0;
}