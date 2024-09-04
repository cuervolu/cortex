package com.cortex.backend.entities.progress;

import com.cortex.backend.entities.education.Module;
import com.cortex.backend.entities.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_module_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModuleProgress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "module_id", nullable = false)
  private Module module;

  @Column(name = "lessons_completed", nullable = false)
  private Integer lessonsCompleted;

  @Column(name = "last_updated", nullable = false)
  private LocalDate lastUpdated;
}