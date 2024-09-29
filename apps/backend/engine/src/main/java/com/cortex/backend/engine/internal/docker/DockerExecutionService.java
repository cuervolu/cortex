package com.cortex.backend.engine.internal.docker;

import com.cortex.backend.core.common.exception.CodeExecutionException;
import com.cortex.backend.core.common.exception.UnsupportedLanguageException;
import com.cortex.backend.core.domain.Language;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectExecResponse;
import com.github.dockerjava.api.model.*;
import com.cortex.backend.engine.api.LanguageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class DockerExecutionService {

  private final DockerClient dockerClient;
  private final LanguageRepository languageRepository;


  public record ExecutionResult(String stdout, String stderr, int exitCode, int executionTime,
                                int memoryUsed) {

  }

  private record CommandResult(int exitCode, String output) {

  }


  public ExecutionResult executeCode(String code, Path exercisePath, String languageName)
      throws CodeExecutionException {
    Language language = languageRepository.findByName(languageName)
        .orElseThrow(
            () -> new UnsupportedLanguageException("Unsupported language: " + languageName));

    String containerId = null;

    try (ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream()) {

      String fileName = "main" + language.getFileExtension();
      containerId = createAndStartContainer(language, exercisePath);

      // Create the file directly in the container
      ExecCreateCmdResponse createFileCmd = dockerClient.execCreateCmd(containerId)
          .withCmd("sh", "-c", "echo '" + code.replace("'", "'\"'\"'") + "' > /code/" + fileName)
          .exec();
      executeCommand(createFileCmd, 5000L, new ByteArrayOutputStream(), new ByteArrayOutputStream());

      // Verify file creation and content
      ExecCreateCmdResponse lsCommand = dockerClient.execCreateCmd(containerId)
          .withCmd("ls", "-la", "/code")
          .exec();
      CommandResult lsResult = executeCommand(lsCommand, 5000L, new ByteArrayOutputStream(), new ByteArrayOutputStream());
      log.info("Contents of /code after file creation: \n{}", lsResult.output());

      ExecCreateCmdResponse catCommand = dockerClient.execCreateCmd(containerId)
          .withCmd("cat", "/code/" + fileName)
          .exec();
      CommandResult catResult = executeCommand(catCommand, 5000L, new ByteArrayOutputStream(), new ByteArrayOutputStream());
      log.info("Content of {}: \n{}", fileName, catResult.output());

      String executeCommand = buildExecuteCommand(language, fileName);
      log.info("Executing command: {}", executeCommand);
      ExecCreateCmdResponse execCreateCmdResponse = createExecCommand(containerId, executeCommand);

      long startTime = System.currentTimeMillis();
      CommandResult commandResult = executeCommand(execCreateCmdResponse,
          language.getDefaultTimeout(), stdout, stderr);
      long endTime = System.currentTimeMillis();

      int executionTime = (int) (endTime - startTime);
      int memoryUsed = getMemoryUsage(containerId);

      return new ExecutionResult(
          stdout.toString(StandardCharsets.UTF_8),
          stderr.toString(StandardCharsets.UTF_8),
          commandResult.exitCode(),
          executionTime,
          memoryUsed
      );
    } catch (IOException | InterruptedException e) {
      log.error("Error executing code in Docker container", e);
      throw new CodeExecutionException("Error executing code in Docker container", e);
    } finally {
      cleanupResources(containerId);
    }
  }
  private String createAndStartContainer(Language language, Path exercisePath)
      throws InterruptedException {
    String containerName = "cortex-" + UUID.randomUUID();
    Volume exerciseVolume = new Volume("/exercise");

    HostConfig hostConfig = new HostConfig()
        .withMemory(language.getDefaultMemoryLimit())
        .withCpuCount(language.getDefaultCpuLimit())
        .withBinds(
            new Bind(exercisePath.toString(), exerciseVolume)
        );

    CreateContainerResponse container = dockerClient.createContainerCmd(language.getDockerImage())
        .withName(containerName)
        .withHostConfig(hostConfig)
        .withCmd("tail", "-f", "/dev/null")
        .withWorkingDir("/code")
        .withTty(true)
        .withAttachStderr(true)
        .withAttachStdout(true)
        .exec();

    String containerId = container.getId();
    dockerClient.startContainerCmd(containerId).exec();
    log.info("Container started successfully: {}", containerId);

    // Ensure /code directory exists and has correct permissions
    ExecCreateCmdResponse mkdirCommand = dockerClient.execCreateCmd(containerId)
        .withCmd("mkdir", "-p", "/code")
        .exec();
    executeCommand(mkdirCommand, 5000L, new ByteArrayOutputStream(), new ByteArrayOutputStream());

    ExecCreateCmdResponse chmodCommand = dockerClient.execCreateCmd(containerId)
        .withCmd("chmod", "777", "/code")
        .exec();
    executeCommand(chmodCommand, 5000L, new ByteArrayOutputStream(), new ByteArrayOutputStream());

    return containerId;
  }

  private ExecCreateCmdResponse createExecCommand(String containerId, String command) {
    return dockerClient.execCreateCmd(containerId)
        .withAttachStdout(true)
        .withAttachStderr(true)
        .withCmd("/bin/sh", "-c", command)
        .exec();
  }


  private CommandResult executeCommand(ExecCreateCmdResponse execCreateCmdResponse, Long timeout,
      ByteArrayOutputStream stdout, ByteArrayOutputStream stderr) throws InterruptedException {
    dockerClient.execStartCmd(execCreateCmdResponse.getId())
        .exec(new ExecStartResultCallback(stdout, stderr))
        .awaitCompletion(timeout, TimeUnit.MILLISECONDS);

    InspectExecResponse inspectExecResponse = dockerClient.inspectExecCmd(
        execCreateCmdResponse.getId()).exec();
    Long exitCodeLong = inspectExecResponse.getExitCodeLong();

    int exitCode = -1;
    if (exitCodeLong != null) {
      exitCode = exitCodeLong.intValue();
    } else {
      log.warn("Exit code is null for command execution. InspectExecResponse: {}",
          inspectExecResponse);
    }

    return new CommandResult(exitCode, stdout.toString(StandardCharsets.UTF_8));
  }

  private String buildExecuteCommand(Language language, String fileName) {
    return language.getExecuteCommand().replace("{fileName}", fileName);
  }

  private Path createTempFile(String fileName, String content) throws IOException {
    Path tempFile = Files.createTempFile("cortex_", fileName);
    Files.writeString(tempFile, content);
    return tempFile;
  }

  private int getMemoryUsage(String containerId) {
    try {
      String[] cmd = {"/bin/sh", "-c",
          "cat /sys/fs/cgroup/memory/memory.usage_in_bytes 2>/dev/null || cat /sys/fs/cgroup/memory.current 2>/dev/null || echo -1"};
      ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
          .withCmd(cmd)
          .exec();

      ByteArrayOutputStream stdout = new ByteArrayOutputStream();
      dockerClient.execStartCmd(execCreateCmdResponse.getId())
          .exec(new ExecStartResultCallback(stdout, null))
          .awaitCompletion(5, TimeUnit.SECONDS);

      String memoryUsageStr = stdout.toString().trim();
      return memoryUsageStr.isEmpty() ? -1
          : Integer.parseInt(memoryUsageStr) / 1024; // Convert bytes to KB
    } catch (Exception e) {
      log.error("Failed to get memory usage for container {}", containerId, e);
      return -1;
    }
  }

  private void cleanupResources(String containerId) {
    try {
      if (containerId != null) {
        dockerClient.stopContainerCmd(containerId).exec();
        dockerClient.removeContainerCmd(containerId).withForce(true).exec();
      }
    } catch (Exception e) {
      log.error("Error cleaning up resources", e);
    }
  }

  private static class ExecStartResultCallback extends
      com.github.dockerjava.api.async.ResultCallback.Adapter<Frame> {

    private final ByteArrayOutputStream stdout;
    private final ByteArrayOutputStream stderr;

    public ExecStartResultCallback(ByteArrayOutputStream stdout, ByteArrayOutputStream stderr) {
      this.stdout = stdout;
      this.stderr = stderr;
    }

    @Override
    public void onNext(Frame frame) {
      if (frame != null) {
        try {
          switch (frame.getStreamType()) {
            case STDOUT:
              if (stdout != null) {
                stdout.write(frame.getPayload());
              }
              break;
            case STDERR:
              if (stderr != null) {
                stderr.write(frame.getPayload());
              }
              break;
            default:
              log.warn("Received frame with unexpected stream type: {}", frame.getStreamType());
          }
        } catch (IOException e) {
          log.error("Error processing frame", e);
        }
      }
    }
  }
}