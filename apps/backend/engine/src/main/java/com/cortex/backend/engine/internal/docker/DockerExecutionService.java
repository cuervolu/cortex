package com.cortex.backend.engine.internal.docker;

import static com.cortex.backend.engine.internal.utils.Constants.*;

import com.cortex.backend.core.common.exception.ContainerExecutionException;
import com.cortex.backend.core.domain.Language;
import com.cortex.backend.engine.api.LanguageRepository;
import com.cortex.backend.engine.config.AutoCloseableContainer;
import com.cortex.backend.engine.internal.environment.LanguageEnvironmentSetup;
import com.cortex.backend.engine.internal.environment.LanguageEnvironmentSetup.WorkspaceSetup;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.LogContainerCmd;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.StreamType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DockerExecutionService {

  private final DockerClient dockerClient;
  private final LanguageRepository languageRepository;
  private final LanguageEnvironmentSetup environmentSetup;

  public ExecutionResult executeCode(String decodedCode, Path exercisePath, String languageName)
      throws IOException {
    Language language = languageRepository.findByName(languageName)
        .orElseThrow(() -> new IllegalArgumentException("Unsupported language: " + languageName));

    String containerId = UUID.randomUUID().toString();
    Path tempDir = Files.createTempDirectory(TMP_PREFIX + containerId);

    try {
      WorkspaceSetup workspace = environmentSetup.setupWorkspace(
          tempDir,
          exercisePath,
          decodedCode,
          language
      );

      log.info("Running container for language: {}", languageName);
      log.info("Workspace directory: {}", workspace.workspaceDir());
      log.info("Project root directory: {}", workspace.projectRoot());

      return runContainer(language, workspace, containerId);
    } catch (Exception e) {
      log.error("Error during code execution setup", e);
      throw e;
    } finally {
      environmentSetup.cleanupDirectory(tempDir);
    }
  }

  private ExecutionResult runContainer(Language language, WorkspaceSetup workspace,
      String containerId) {
    String workingDir = "/workspace";
    String projectRoot = workspace.projectRoot().toAbsolutePath().toString();

    HostConfig hostConfig = HostConfig.newHostConfig()
        .withBinds(Bind.parse(projectRoot + ":" + workingDir + ":rw"))
        .withMemory(language.getDefaultMemoryLimit())
        .withCpuCount(language.getDefaultCpuLimit());

    try {

      CreateContainerResponse container = dockerClient.createContainerCmd(language.getDockerImage())
          .withHostConfig(hostConfig)
          .withName("code-execution-" + containerId)
          .withWorkingDir(workingDir)
          .withCmd("/bin/sh", "-c", language.getExecuteCommand())
          .exec();

      try (AutoCloseableContainer _ = new AutoCloseableContainer(container, dockerClient)) {
        dockerClient.startContainerCmd(container.getId()).exec();

        int exitCode = waitForContainer(container.getId(), language.getDefaultTimeout());
        ContainerLogs logs = collectLogs(container.getId(), language.getDefaultTimeout());

        return new ExecutionResult(
            exitCode,
            logs.stdout(),
            logs.stderr(),
            0L,
            0L
        );
      }
    } catch (Exception e) {
      log.error("Error executing container", e);
      return new ExecutionResult(-1, "", e.getMessage(), 0L, 0L);
    }
  }


  private int waitForContainer(String containerId, long timeout) {
    try {
      return dockerClient.waitContainerCmd(containerId)
          .exec(new WaitContainerResultCallback())
          .awaitStatusCode(timeout, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      log.error("Error waiting for container", e);
      return -1;
    }
  }

  private ContainerLogs collectLogs(String containerId, long timeout) {
    ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    ByteArrayOutputStream stderr = new ByteArrayOutputStream();

    try {
      LogContainerCmd logCmd = dockerClient.logContainerCmd(containerId)
          .withStdOut(true)
          .withStdErr(true)
          .withTail(1000);

      logCmd.exec(new ResultCallback.Adapter<Frame>() {
        @Override
        public void onNext(Frame item) {
          if (item.getStreamType() == StreamType.STDOUT) {
            stdout.writeBytes(item.getPayload());
          } else if (item.getStreamType() == StreamType.STDERR) {
            stderr.writeBytes(item.getPayload());
          }
        }
      }).awaitCompletion(timeout, TimeUnit.MILLISECONDS);

      return new ContainerLogs(stdout.toString(), stderr.toString());
    } catch (Exception e) {
      log.error("Error collecting logs", e);
      return new ContainerLogs("", "Error collecting logs: " + e.getMessage());
    }
  }

  private record ContainerLogs(String stdout, String stderr) {}

  public record ExecutionResult(int exitCode, String stdout, String stderr,
                                long executionTime, long memoryUsed) {}
}