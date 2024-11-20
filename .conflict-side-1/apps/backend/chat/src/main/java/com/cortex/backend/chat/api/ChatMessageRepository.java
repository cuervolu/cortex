package com.cortex.backend.chat.api;

import com.cortex.backend.core.domain.ChatMessage;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, Long> {

  List<ChatMessage> findByChatId(String s);
}
