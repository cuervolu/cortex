package com.cortex.backend.entities.education;

import com.cortex.backend.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "roadmap")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Roadmap extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(nullable = false)
  private String slug;

  @ManyToMany
  @JoinTable(
      name = "roadmap_tags",
      joinColumns = @JoinColumn(name = "roadmap_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id")
  )
  private Set<Tag> tags;

  @ManyToMany
  @JoinTable(
      name = "roadmap_courses",
      joinColumns = @JoinColumn(name = "roadmap_id"),
      inverseJoinColumns = @JoinColumn(name = "course_id")
  )
  private Set<Course> courses;
}