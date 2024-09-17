package com.cortex.backend.blog.domain;

import com.cortex.backend.entities.BaseEntity;
import com.cortex.backend.education.domain.Tag;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

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