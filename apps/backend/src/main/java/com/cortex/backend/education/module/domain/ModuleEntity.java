package com.cortex.backend.education.module.domain;

import com.cortex.backend.education.course.domain.Course;
import com.cortex.backend.education.lesson.domain.Lesson;
import com.cortex.backend.entities.BaseEntity;
import com.cortex.backend.media.domain.Media;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "module")
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "image_id")
  private Media image;

  @Column(nullable = false)
  private String slug;

  @OneToMany(mappedBy = "moduleEntity", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Lesson> lessons;
}