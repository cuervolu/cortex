package com.cortex.backend.entities.discussions;

import com.cortex.backend.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Category extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String name;

  private String description;

  @ManyToMany
  @JoinTable(
      name = "forum_categories",
      joinColumns = @JoinColumn(name = "category_id"),
      inverseJoinColumns = @JoinColumn(name = "forum_id"),
      indexes = @Index(name = "forum_categories_index_0", columnList = "forum_id, category_id")
  )
  private Set<Forum> forums;
}