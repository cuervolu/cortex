package com.cortex.backend.chat.internal;

import com.cortex.backend.chat.api.ChatMessageRepository;
import com.cortex.backend.core.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

  private final ChatMessageRepository repository;
  private final ChatRoomService chatRoomService;

  public ChatMessage save(ChatMessage chatMessage) {
    chatMessage.setChatId(chatMessage.getMentorship().getId().toString());
    return repository.save(chatMessage);
  }

  public List<ChatMessage> findChatMessagesByMentorship(Long mentorshipId) {
    return repository.findByChatId(mentorshipId.toString());
  }
}