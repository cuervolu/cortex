package com.cortex.backend.chat.internal;

import com.cortex.backend.chat.api.dto.ChatMessageRequest;
import com.cortex.backend.chat.api.dto.ChatRoomCreationDTO;
import com.cortex.backend.core.domain.ChatMessage;
import com.cortex.backend.core.domain.Mentorship;
import com.cortex.backend.education.mentorship.api.MentorshipRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Mapper(componentModel = "spring",
    uses = {ChatRoomService.class},
    imports = {Date.class})
public abstract class ChatMessageMapper {

  @Autowired
  private MentorshipRepository mentorshipRepository;

  @Autowired
  private ChatRoomService chatRoomService;

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "mentorship", source = "request.mentorshipId", qualifiedByName = "mentorshipIdToMentorship")
  @Mapping(target = "chatId", expression = "java(getChatRoomId(request.mentorshipId(), request.senderId(), request.recipientId()))")
  @Mapping(target = "senderId", source = "request.senderId")
  @Mapping(target = "recipientId", source = "request.recipientId")
  @Mapping(target = "timestamp", expression = "java(new Date())")
  public abstract ChatMessage toChatMessage(ChatMessageRequest request);

  @Named("mentorshipIdToMentorship")
  protected Mentorship mentorshipIdToMentorship(Long mentorshipId) {
    return mentorshipRepository.findById(mentorshipId)
        .orElseThrow(() -> new RuntimeException("Mentorship not found"));
  }

  protected String getChatRoomId(Long mentorshipId, Long senderId, Long recipientId) {
    return chatRoomService.getChatRoomId(senderId, recipientId)
        .orElseGet(() -> chatRoomService.createChatRoom(
            new ChatRoomCreationDTO(mentorshipId, senderId, recipientId)));
  }
}