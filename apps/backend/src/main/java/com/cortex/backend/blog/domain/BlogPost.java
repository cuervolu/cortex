package com.cortex.backend.blog.domain;

import com.cortex.backend.education.domain.Tag;
import com.cortex.backend.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "blog_post")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPost extends BaseEntity {

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false)
  private String slug;

  @Column(name = "published_at")
  private LocalDateTime publishedAt;

  @ManyToMany
  @JoinTable(
      name = "blog_post_tags",
      joinColumns = @JoinColumn(name = "blog_post_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id")
  )
  private Set<Tag> tags;

  @Column(name = "featured_image_url")
  private String featuredImageUrl;

  @Column(name = "is_published")
  private boolean isPublished;
}