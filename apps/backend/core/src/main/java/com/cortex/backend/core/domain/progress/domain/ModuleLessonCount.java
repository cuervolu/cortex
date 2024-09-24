package com.cortex.backend.core.domain.progress.domain;

import com.cortex.backend.core.domain.ModuleEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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