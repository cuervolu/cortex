package com.cortex.backend.education.course.domain;

import com.cortex.backend.entities.BaseEntity;
import com.cortex.backend.education.module.domain.ModuleEntity;
import com.cortex.backend.education.roadmap.domain.Roadmap;
import com.cortex.backend.education.domain.Tag;
import com.cortex.backend.media.domain.Media;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "course")
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

  @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ModuleEntity> moduleEntities;
}