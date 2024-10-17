package com.cortex.backend.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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