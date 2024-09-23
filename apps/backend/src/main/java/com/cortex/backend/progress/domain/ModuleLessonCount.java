package com.cortex.backend.progress.domain;

import com.cortex.backend.education.module.domain.ModuleEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "module_lesson_count")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleLessonCount {

  @Id
  @Column(name = "module_id")
  private Long moduleId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "module_id")
  private ModuleEntity moduleEntity;

  @Column(name = "total_lessons", nullable = false)
  private Integer totalLessons;
}