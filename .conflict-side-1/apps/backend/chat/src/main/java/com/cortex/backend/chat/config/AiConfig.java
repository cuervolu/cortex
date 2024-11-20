package com.cortex.backend.chat.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AiConfig {

  @Bean
  public ChatClient chatClient(ChatModel chatModel) {
    InMemoryChatMemory memory = new InMemoryChatMemory();
    return ChatClient.builder(chatModel)
        .defaultAdvisors(
            new PromptChatMemoryAdvisor(memory),
            new MessageChatMemoryAdvisor(memory))
        .build();
  }
}