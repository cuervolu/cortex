package com.cortex.backend.chat.api;

import com.cortex.backend.chat.internal.AiChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat/ai")
public class AiChatController {

  private final AiChatService aiChatService;

  public AiChatController(AiChatService aiChatService) {
    this.aiChatService = aiChatService;
  }

  @GetMapping
  public Flux<String> chatWithStream(@RequestParam String message,
      @RequestParam String exerciseSlug,
      @RequestParam(required = false) String userCode) {
    return aiChatService.getChatStream(message, exerciseSlug, userCode);
  }
}