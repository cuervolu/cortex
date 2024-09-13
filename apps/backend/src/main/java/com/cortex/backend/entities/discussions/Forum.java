package com.cortex.backend.entities.discussions;

import com.cortex.backend.entities.BaseEntity;
import com.cortex.backend.entities.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "forums")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Forum extends BaseEntity {

  @ManyToMany(mappedBy = "forums")
  private Set<Category> categories;

  @Column(nullable = false, unique = true)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @ManyToMany
  @JoinTable(
      name = "forum_moderators",
      joinColumns = @JoinColumn(name = "forum_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private Set<User> moderators;

  @OneToMany(mappedBy = "forum")
  private Set<ForumPost> posts;
}