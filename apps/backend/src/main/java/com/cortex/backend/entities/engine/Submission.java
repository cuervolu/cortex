package com.cortex.backend.entities.engine;

import com.cortex.backend.entities.BaseEntity;
import com.cortex.backend.entities.education.Solution;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "submission")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Submission extends BaseEntity {

  @Column(nullable = false, columnDefinition = "TEXT")
  private String code;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "language_id", nullable = false)
  private Language language;

  @Column(columnDefinition = "TEXT")
  private String stdin;

  @Column(name = "expected_output", columnDefinition = "TEXT")
  private String expectedOutput;

  @Column(name = "cpu_time_limit")
  private Float cpuTimeLimit;

  @Column(name = "cpu_extra_time")
  private Float cpuExtraTime;

  @Column(name = "command_line_arguments")
  private String commandLineArguments;

  @Column(name = "compiler_options")
  private String compilerOptions;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "solution_id", nullable = false)
  private Solution solution;
}