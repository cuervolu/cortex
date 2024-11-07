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
@Table(name = "lesson", indexes = {
    @Index(name = "idx_lesson_name", columnList = "name"),
    @Index(name = "idx_lesson_slug", columnList = "slug"),
    @Index(name = "idx_lesson_is_published", columnList = "is_published"),
    @Index(name = "idx_lesson_module", columnList = "module_id"),
    @Index(name = "idx_lesson_module_order", columnList = "module_id,display_order"),
    @Index(name = "idx_lesson_credits", columnList = "credits"),
    @Index(name = "idx_lesson_created_at", columnList = "created_at")
})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Lesson extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "module_id", nullable = false)
  private ModuleEntity moduleEntity;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false)
  private Integer credits;

  @Column(nullable = false)
  private String slug;
  
  @Column(name="is_published", nullable = false)
  private Boolean isPublished = false;

  @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Exercise> exercises;

  @Column(name = "display_order", nullable = false)
  private Integer displayOrder = 0;
}