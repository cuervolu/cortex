package com.cortex.backend.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "chat_rooms")
public class ChatRoom {
  @Id
  @GeneratedValue private Long id;
  
  @Column(name = "chat_id")
  private String chatId;
  
  @Column(name = "sender_id")
  private Long senderId;
  
  @Column(name = "recipent_id")
  private Long recipentId;

}
