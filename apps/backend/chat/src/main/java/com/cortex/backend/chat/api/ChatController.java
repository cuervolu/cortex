package com.cortex.backend.chat.api;

import com.cortex.backend.chat.api.dto.ChatMessageRequest;
import com.cortex.backend.chat.api.dto.ChatRoomCreationDTO;
import com.cortex.backend.chat.internal.ChatMessageService;
import com.cortex.backend.chat.internal.ChatRoomService;
import com.cortex.backend.core.domain.ChatMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
@Tag(name = "Chat", description = "Chat endpoints for sending and receiving messages between user and mentor")
public class ChatController {

  private final SimpMessagingTemplate messagingTemplate;
  private final ChatMessageService chatMessageService;
  private final ChatRoomService chatRoomService;

  @MessageMapping("/chat")
  public void processMessage(@Payload @Valid ChatMessageRequest chatMessageRequest) {
    ChatMessage savedMsg = chatMessageService.save(chatMessageRequest);
    messagingTemplate.convertAndSendToUser(
        String.valueOf(chatMessageRequest.recipientId()), "/queue/messages",
        ChatNotification.builder()
            .id(savedMsg.getId())
            .senderId(savedMsg.getSenderId())
            .recipientId(savedMsg.getRecipientId())
            .content(savedMsg.getContent())
            .build());
  }

  @Operation(summary = "Create a chat room",
      description = "Creates a new chat room for a mentorship",
      responses = {
          @ApiResponse(responseCode = "200", description = "Successfully created chat room",
              content = @Content(schema = @Schema(implementation = String.class)))
      })
  @PostMapping("/room")
  public ResponseEntity<String> createChatRoom(
      @Parameter(description = "Chat room creation details")
      @RequestBody ChatRoomCreationDTO chatRoomCreationDTO) {
    String chatId = chatRoomService.createChatRoom(chatRoomCreationDTO);
    return ResponseEntity.ok(chatId);
  }

  @Operation(summary = "Find chat messages",
      description = "Retrieves all chat messages for a specific mentorship",
      responses = {
          @ApiResponse(responseCode = "200", description = "Successfully retrieved chat messages",
              content = @Content(schema = @Schema(implementation = ChatMessage.class)))
      })
  @GetMapping("/messages/{mentorshipId}")
  public ResponseEntity<List<ChatMessage>> findChatMessages(
      @Parameter(description = "ID of the mentorship")
      @PathVariable Long mentorshipId) {
    return ResponseEntity.ok(chatMessageService.findChatMessagesByMentorship(mentorshipId));
  }

  @MessageMapping("/chat.mentorship")
  public void sendMentorshipMessage(@Payload @Valid ChatMessageRequest chatMessageRequest) {
    ChatMessage savedMsg = chatMessageService.save(chatMessageRequest);
    messagingTemplate.convertAndSendToUser(
        String.valueOf(chatMessageRequest.mentorshipId()), "/queue/mentorship",
        ChatNotification.builder()
            .id(savedMsg.getId())
            .senderId(savedMsg.getSenderId())
            .recipientId(savedMsg.getRecipientId())
            .content(savedMsg.getContent())
            .build());
  }
}