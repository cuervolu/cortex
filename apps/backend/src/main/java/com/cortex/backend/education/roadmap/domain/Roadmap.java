package com.cortex.backend.education.roadmap.domain;

import com.cortex.backend.education.course.domain.Course;
import com.cortex.backend.entities.BaseEntity;
import com.cortex.backend.education.domain.Tag;
import com.cortex.backend.media.domain.Media;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "image_id")
  private Media image;

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