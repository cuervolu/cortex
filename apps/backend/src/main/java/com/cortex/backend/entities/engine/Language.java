package com.cortex.backend.entities.engine;

import com.cortex.backend.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "language")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Language extends BaseEntity {

  @Column(unique = true, nullable = false)
  private String name;

  @Column(nullable = false, name = "docker_image")
  private String dockerImage;

  @Column(nullable = false, name = "execute_command")
  private String executeCommand;

  @Column(nullable = false, name = "file_extension")
  private String fileExtension;

  @Column(name = "compile_command")
  private String compileCommand;

  @Column(name = "default_memory_limit")
  private Long defaultMemoryLimit;

  @Column(name = "default_cpu_limit")
  private Long defaultCpuLimit;

  @Column(name = "default_timeout")
  private Long defaultTimeout;
}