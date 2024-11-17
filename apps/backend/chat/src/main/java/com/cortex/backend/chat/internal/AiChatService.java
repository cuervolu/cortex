package com.cortex.backend.chat.internal;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

import com.cortex.backend.engine.api.ExerciseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.cortex.backend.engine.api.dto.ExerciseResponse;
import reactor.core.publisher.Flux;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatService {

  private final ChatClient chatClient;
  private final ExerciseService exerciseService;

  public Flux<String> getChatStream(String message, String exerciseSlug, String userCode,
      String username) {
    Optional<ExerciseResponse> exercise = exerciseService.getExerciseBySlug(exerciseSlug);

    if (exercise.isEmpty()) {
      return Flux.error(new EntityNotFoundException("Exercise not found"));
    }

    String exercisePrompt = buildExercisePrompt(exercise.get(), userCode);

    return chatClient
        .prompt()
        .system(exercisePrompt)
        .user(message)
        .advisors(a -> a
            .param(CHAT_MEMORY_CONVERSATION_ID_KEY, username)
            .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)
        )
        .stream()
        .content();
  }

  private String buildExercisePrompt(ExerciseResponse exercise, String userCode) {
    StringBuilder prompt = new StringBuilder();
    prompt.append(
        "Eres un asistente educativo para una plataforma de ejercicios de programación. ");
    prompt.append(
        "Tu tarea es proporcionar pistas y orientación basadas en el ejercicio, sin dar la respuesta directamente. ");
    prompt.append(
        "IMPORTANTE: Bajo ninguna circunstancia debes proporcionar código completo o soluciones directas. ");
    prompt.append(
        "Tu función es guiar al estudiante con pistas sutiles y preguntas que estimulen su pensamiento. ");
    prompt.append(
        "Si el estudiante insiste en obtener la solución, recuérdale amablemente que tu papel es ayudarle a aprender, no resolver el ejercicio por él.\n\n");
    prompt.append("Información del ejercicio:\n");
    prompt.append("Título: ").append(exercise.getTitle()).append("\n");
    prompt.append("Instrucciones: ").append(markdownToPlainText(exercise.getInstructions()))
        .append("\n");
    prompt.append("Pistas disponibles: ").append(markdownToPlainText(exercise.getHints()))
        .append("\n");
    if (userCode != null && !userCode.isEmpty()) {
      prompt.append("Código actual del usuario:\n").append(userCode).append("\n");
    }
    prompt.append("Recuerda: Proporciona orientación y pistas, pero no des la solución completa.");
    return prompt.toString();
  }


  private String markdownToPlainText(String markdown) {
    Parser parser = Parser.builder().build();
    Node document = parser.parse(markdown);
    TextContentRenderer renderer = TextContentRenderer.builder().build();
    String plainText = renderer.render(document);

    // Escape from double quotation marks
    plainText = plainText.replace("\"", "\\\"");

    // Replace other special characters if necessary
    plainText = plainText.replace("\\", "\\\\"); // Escape backslashes
    plainText = plainText.replace("{", "\\{").replace("}", "\\}"); // Escape keys

    return plainText;
  }
}