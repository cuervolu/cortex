package com.cortex.backend.education.roadmap.domain;

import com.cortex.backend.education.course.domain.Course;
import com.cortex.backend.education.domain.Tag;
import com.cortex.backend.entities.BaseEntity;
import com.cortex.backend.media.domain.Media;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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