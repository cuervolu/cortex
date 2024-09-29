package com.cortex.backend.engine.internal.services;

import static com.cortex.backend.engine.internal.utils.Constants.*;

import com.cortex.backend.core.common.exception.CodeExecutionException;
import com.cortex.backend.core.common.exception.ContentChangedException;
import com.cortex.backend.core.common.exception.ResultNotAvailableException;
import com.cortex.backend.core.common.exception.UnsupportedLanguageException;
import com.cortex.backend.core.domain.Exercise;
import com.cortex.backend.engine.api.LanguageRepository;
import com.cortex.backend.engine.api.ExerciseRepository;
import com.cortex.backend.engine.api.dto.CodeExecutionRequest;
import com.cortex.backend.engine.api.dto.CodeExecutionResult;
import com.cortex.backend.engine.api.dto.CodeExecutionTask;
import com.cortex.backend.engine.api.dto.TestCaseResult;
import com.cortex.backend.engine.internal.docker.DockerExecutionService;
import com.cortex.backend.engine.internal.utils.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeExecutionService {

  private final LanguageRepository languageRepository;
  private final ExerciseRepository exerciseRepository;
  private final RabbitTemplate rabbitTemplate;
  private final RedisTemplate<String, CodeExecutionResult> redisTemplate;
  private final DockerExecutionService dockerExecutionService;

  @Value("${github.exercises.local-path}")
  private String localExercisesPath;

  public String submitCodeExecution(CodeExecutionRequest request) {
    Exercise exercise = exerciseRepository.findById(request.exerciseId())
        .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
    if (!languageRepository.existsByName(request.language())) {
      throw new UnsupportedLanguageException("Unsupported language: " + request.language());
    }

    String taskId = UUID.randomUUID().toString();
    String contentHash = HashUtil.generateSHA256Hash(request.code() + exercise.getGithubPath());

    CodeExecutionTask task = new CodeExecutionTask(taskId, request, contentHash, exercise.getGithubPath());
    rabbitTemplate.convertAndSend(CODE_EXECUTION_QUEUE, task);

    return taskId;
  }

  public CodeExecutionResult getExecutionResult(String taskId) {
    CodeExecutionResult result = redisTemplate.opsForValue().get(RESULT_KEY_PREFIX + taskId);

    if (result == null) {
      log.error("Execution result not available yet for task: {}", taskId);
      throw new ResultNotAvailableException("Execution result not available yet");
    }

    return result;
  }

  public void processCodeExecution(CodeExecutionTask task) {
    try {
      String currentHash = HashUtil.generateSHA256Hash(task.request().code() + task.githubPath());
      if (!currentHash.equals(task.contentHash())) {
        throw new ContentChangedException("Content has changed since submission");
      }

      CodeExecutionResult result = executeCode(task.request(), task.githubPath());
      redisTemplate.opsForValue().set(
          RESULT_KEY_PREFIX + task.taskId(),
          result,
          RESULT_EXPIRATION_HOURS,
          TimeUnit.HOURS
      );
    } catch (Exception e) {
      log.error("Error processing code execution task", e);

      CodeExecutionResult errorResult = CodeExecutionResult.builder()
          .success(false)
          .language(task.request().language())
          .stderr("Internal error: " + e.getMessage())
          .build();

      redisTemplate.opsForValue().set(
          RESULT_KEY_PREFIX + task.taskId(),
          errorResult,
          RESULT_EXPIRATION_HOURS,
          TimeUnit.HOURS
      );
    }
  }

  private CodeExecutionResult executeCode(CodeExecutionRequest request, String githubPath) {
    try {
      String decodedCode = new String(Base64.getDecoder().decode(request.code()));
      Path exercisePath = Paths.get(localExercisesPath, githubPath);

      log.info("Executing code for language: {}", request.language());
      log.info("Exercise path: {}", exercisePath);
      log.info("Decoded code length: {}", decodedCode.length());
      log.info("Decoded code content: \n{}", decodedCode);

      DockerExecutionService.ExecutionResult dockerResult = dockerExecutionService.executeCode(
          decodedCode,
          exercisePath,
          request.language()
      );

      log.info("Docker execution result - Exit code: {}", dockerResult.exitCode());
      log.info("Docker execution stdout: \n{}", dockerResult.stdout());
      log.info("Docker execution stderr: \n{}", dockerResult.stderr());

      List<TestCaseResult> testCaseResults = parseTestResults(dockerResult.stdout(), request.language());

      boolean success = dockerResult.exitCode() == 0;

      return CodeExecutionResult.builder()
          .success(success)
          .stdout(dockerResult.stdout())
          .stderr(dockerResult.stderr())
          .executionTime(dockerResult.executionTime())
          .language(request.language())
          .memoryUsed(dockerResult.memoryUsed())
          .testCaseResults(testCaseResults)
          .build();
    } catch (CodeExecutionException e) {
      log.error("Error executing code in Docker container", e);
      return CodeExecutionResult.builder()
          .success(false)
          .stderr("Error executing code in Docker container: " + e.getMessage())
          .language(request.language())
          .build();
    } catch (Exception e) {
      log.error("Unexpected error executing code", e);
      return CodeExecutionResult.builder()
          .success(false)
          .stderr("Unexpected error: " + e.getMessage())
          .language(request.language())
          .build();
    }
  }

  private List<TestCaseResult> parseTestResults(String output, String language) {
    // TODO: Implement parsing logic for test results based on the output format and language
    // This will depend on how your test runner for each language formats its output
    return List.of(); // Placeholder
  }
}