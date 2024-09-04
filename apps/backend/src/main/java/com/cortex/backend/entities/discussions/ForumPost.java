package com.cortex.backend.entities.discussions;

import com.cortex.backend.entities.BaseEntity;
import com.cortex.backend.entities.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "forum_posts",
    indexes = @Index(name = "forum_posts_index_0", columnList = "parent_post_id, forum_id, user_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ForumPost extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "forum_id", nullable = false)
  private Forum forum;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_post_id")
  private ForumPost parentPost;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;
}