package com.cortex.backend.discussions.domain;

import com.cortex.backend.entities.BaseEntity;
import com.cortex.backend.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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