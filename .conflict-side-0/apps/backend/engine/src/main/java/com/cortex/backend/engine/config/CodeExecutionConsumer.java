package com.cortex.backend.engine.config;

import com.cortex.backend.engine.api.dto.CodeExecutionTask;
import com.cortex.backend.engine.internal.services.CodeExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.cortex.backend.engine.internal.utils.Constants.CODE_EXECUTION_QUEUE;

/**
 * Service class for consuming code execution tasks from a RabbitMQ queue.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CodeExecutionConsumer {

  private final CodeExecutionService codeExecutionService;

  /**
   * Consumes a code execution task from the specified RabbitMQ queue.
   *
   * @param task the code execution task to process
   */
  @RabbitListener(queues = CODE_EXECUTION_QUEUE)
  public void consumeCodeExecutionTask(CodeExecutionTask task) {
    log.info("Received code execution task: {}", task.taskId());
    try {
      codeExecutionService.processCodeExecution(task);
    } catch (Exception e) {
      log.error("Error processing code execution task: {}", task.taskId(), e);
    }
  }
}