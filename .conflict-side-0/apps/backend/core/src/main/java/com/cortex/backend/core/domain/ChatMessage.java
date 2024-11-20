package com.cortex.backend.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chat_messages",
    indexes = @Index(name = "chat_messages_index_0", columnList = "mentorship_id, sender_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentorship_id", nullable = false)
  private Mentorship mentorship;

  @Column(nullable = false, name = "chat_id")
  private String chatId;

  @Column(nullable = false, name = "sender_id")
  private Long senderId;

  @Column(nullable = false, name = "recipient_id")
  private Long recipientId;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;
  
  private Date timestamp;
}