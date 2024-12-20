package com.cortex.backend.engine.internal.services;

import static com.cortex.backend.engine.internal.utils.Constants.CODE_EXECUTION_QUEUE;
import static com.cortex.backend.engine.internal.utils.Constants.RESULT_EXPIRATION_HOURS;
import static com.cortex.backend.engine.internal.utils.Constants.RESULT_KEY_PREFIX;

import com.cortex.backend.core.common.BusinessErrorCodes;
import com.cortex.backend.core.common.exception.InvalidExerciseStateException;
import com.cortex.backend.core.common.exception.ResultNotAvailableException;
import com.cortex.backend.core.common.exception.UnsupportedLanguageException;
import com.cortex.backend.core.domain.EntityType;
import com.cortex.backend.core.domain.Exercise;
import com.cortex.backend.core.domain.ExerciseStatus;
import com.cortex.backend.core.domain.Lesson;
import com.cortex.backend.education.progress.api.LessonCompletedEvent;
import com.cortex.backend.education.progress.api.ProgressTrackingService;
import com.cortex.backend.engine.api.ExerciseRepository;
import com.cortex.backend.engine.api.LanguageRepository;
import com.cortex.backend.engine.api.SubmissionService;
import com.cortex.backend.engine.api.dto.CodeExecutionRequest;
import com.cortex.backend.engine.api.dto.CodeExecutionResult;
import com.cortex.backend.engine.api.dto.CodeExecutionTask;
import com.cortex.backend.engine.api.dto.SubmissionResponse;
import com.cortex.backend.engine.api.dto.TestCaseResult;
import com.cortex.backend.engine.internal.docker.DockerExecutionService;
import com.cortex.backend.engine.internal.parser.TestResultParser;
import com.cortex.backend.engine.internal.parser.TestResultParserFactory;
import jakarta.persistence.EntityNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CodeExecutionService {

  private final LanguageRepository languageRepository;
  private final ExerciseRepository exerciseRepository;
  private final RabbitTemplate rabbitTemplate;
  private final RedisTemplate<String, CodeExecutionResult> redisTemplate;
  private final DockerExecutionService dockerExecutionService;
  private final SubmissionService submissionService;
  private final ProgressTrackingService progressTrackingService;
  private final ApplicationEventPublisher eventPublisher;

  @Value("${github.exercises.local-path}")
  private String localExercisesPath;

  @Transactional
  public String submitCodeExecution(CodeExecutionRequest request, Long userId) {
    Exercise exercise = exerciseRepository.findById(request.exerciseId())
        .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));

    validateExerciseState(exercise);

    if (!languageRepository.existsByName(request.language())) {
      throw new UnsupportedLanguageException("Unsupported language: " + request.language());
    }

    String taskId = UUID.randomUUID().toString();
    SubmissionResponse submission = submissionService.createSubmission(request, userId);
    CodeExecutionTask task = new CodeExecutionTask(taskId, request,
        exercise.getGithubPath(), submission.getId(), userId);
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

  @Transactional
  public void processCodeExecution(CodeExecutionTask task) {
    try {
      Exercise exercise = exerciseRepository.findById(task.request().exerciseId())
          .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

      validateExerciseState(exercise);

      CodeExecutionResult result = executeCode(task.request(), task.githubPath());
      submissionService.updateSubmissionWithResult(task.submissionId(), result);

      if (result.isSuccess()) {
        progressTrackingService.trackProgressAndCheckCompletion(
            task.userId(),
            exercise.getId(),
            EntityType.EXERCISE,
            exercise.getLesson() // Ya validamos que existe
        );
      }

      saveExecutionResult(task.taskId(), result);

    } catch (Exception e) {
      log.error("Error processing code execution task: {}", e.getMessage(), e);
      handleExecutionError(task, e);
    }
  }

  private void saveExecutionResult(String taskId, CodeExecutionResult result) {
    redisTemplate.opsForValue().set(
        RESULT_KEY_PREFIX + taskId,
        result,
        RESULT_EXPIRATION_HOURS,
        TimeUnit.HOURS
    );
  }

  private void handleExecutionError(CodeExecutionTask task, Exception e) {
    CodeExecutionResult errorResult = CodeExecutionResult.builder()
        .success(false)
        .language(task.request().language())
        .stderr("Internal error: " + e.getMessage())
        .exerciseId(task.request().exerciseId())
        .build();

    saveExecutionResult(task.taskId(), errorResult);
  }


  private CodeExecutionResult executeCode(CodeExecutionRequest request, String githubPath) {
    try {
      String decodedCode = new String(Base64.getDecoder().decode(request.code()));
      Path exercisePath = Paths.get(localExercisesPath, githubPath);

      log.info("Executing code for language: {}", request.language());
      log.info("Exercise path: {}", exercisePath);
      log.info("Decoded code length: {}", decodedCode.length());
      log.debug("Decoded code content: \n{}", decodedCode);

      DockerExecutionService.ExecutionResult dockerResult = dockerExecutionService.executeCode(
          decodedCode,
          exercisePath,
          request.language()
      );
      Thread.sleep(500);
      log.info("Docker execution result - Exit code: {}", dockerResult.exitCode());
      log.info("Docker execution stdout: \n{}", dockerResult.stdout());
      log.info("Docker execution stderr: \n{}", dockerResult.stderr());

      List<TestCaseResult> testCaseResults = parseTestResults(
          dockerResult.stdout(),
          dockerResult.stderr(),
          request.language()
      );

      return CodeExecutionResult.builder()
          .success(dockerResult.exitCode() == 0)
          .stdout(dockerResult.stdout())
          .stderr(dockerResult.stderr())
          .executionTime((int) dockerResult.executionTime())
          .language(request.language())
          .memoryUsed((int) dockerResult.memoryUsed())
          .exerciseId(request.exerciseId())  
          .testCaseResults(testCaseResults)
          .build();
    } catch (Exception e) {
      log.error("Unexpected error executing code", e);
      return CodeExecutionResult.builder()
          .success(false)
          .stderr("Unexpected error: " + e.getMessage())
          .language(request.language())
          .exerciseId(request.exerciseId())  
          .build();
    }
  }

  private void validateExerciseState(Exercise exercise) {
    if (exercise.getStatus() != ExerciseStatus.PUBLISHED) {
      throw new InvalidExerciseStateException(
          String.format("Cannot submit code for exercise %d - current status: %s",
              exercise.getId(),
              exercise.getStatus()),
          BusinessErrorCodes.EXERCISE_NOT_PUBLISHED
      );
    }

    if (exercise.getLesson() == null) {
      throw new InvalidExerciseStateException(
          String.format("Cannot submit code for exercise %d - no lesson assigned",
              exercise.getId()),
          BusinessErrorCodes.EXERCISE_NO_LESSON
      );
    }
  }

  private List<TestCaseResult> parseTestResults(String stdout, String stderr, String language) {
    TestResultParser parser = TestResultParserFactory.getParser(language);
    return parser.parseTestResults(stdout + "\n" + stderr);
  }
}