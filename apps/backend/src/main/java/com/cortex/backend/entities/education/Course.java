package com.cortex.backend.entities.education;

import com.cortex.backend.entities.BaseEntity;
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

  @Column(name = "image_url")
  private String imageUrl;

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
  private Set<Module> modules;
}