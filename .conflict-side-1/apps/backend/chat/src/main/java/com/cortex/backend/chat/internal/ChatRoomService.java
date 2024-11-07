package com.cortex.backend.chat.internal;

import com.cortex.backend.chat.api.ChatRoomRepository;
import com.cortex.backend.chat.api.dto.ChatRoomCreationDTO;
import com.cortex.backend.core.domain.ChatRoom;
import com.cortex.backend.core.domain.Mentorship;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

  private final ChatRoomRepository chatRoomRepository;

  public String createChatRoom(ChatRoomCreationDTO dto) {
    String chatId = String.format("mentorship_%d", dto.mentorshipId());

    ChatRoom mentorMentee = ChatRoom.builder()
        .chatId(chatId)
        .senderId(dto.mentorId())
        .recipentId(dto.menteeId())
        .build();

    ChatRoom menteeMentor = ChatRoom.builder()
        .chatId(chatId)
        .senderId(dto.menteeId())
        .recipentId(dto.mentorId())
        .build();

    chatRoomRepository.save(mentorMentee);
    chatRoomRepository.save(menteeMentor);

    return chatId;
  }
  public Optional<String> getChatRoomId(Long senderId, Long recipientId) {
    return chatRoomRepository.findBySenderIdAndRecipentId(senderId, recipientId)
        .map(ChatRoom::getChatId);
  }
}