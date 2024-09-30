package com.cortex.backend.engine.internal.docker;

import com.cortex.backend.core.common.exception.ContainerExecutionException;
import com.cortex.backend.core.domain.Language;
import com.cortex.backend.engine.api.LanguageRepository;
import com.cortex.backend.engine.config.AutoCloseableContainer;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.LogContainerCmd;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.StreamType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DockerExecutionService {

  private final DockerClient dockerClient;
  private final LanguageRepository languageRepository;

  public ExecutionResult executeCode(String decodedCode, Path exercisePath, String languageName)
      throws IOException {
    Language language = languageRepository.findByName(languageName)
        .orElseThrow(() -> new IllegalArgumentException("Unsupported language: " + languageName));

    String containerId = UUID.randomUUID().toString();
    Path tempDir = Files.createTempDirectory("code-execution-" + containerId);
    Path codePath = tempDir.resolve("code");
    Path exerciseTestPath = tempDir.resolve("exercise");

    try {
      setupExecutionEnvironment(decodedCode, exercisePath, codePath, exerciseTestPath, language);
      return runContainer(language, codePath, exerciseTestPath, containerId);
    } finally {
      cleanupTempDirectory(tempDir);
    }
  }

  private void setupExecutionEnvironment(String decodedCode, Path exercisePath, Path codePath,
      Path exerciseTestPath, Language language) throws IOException {
    Files.createDirectories(codePath);
    Files.createDirectories(exerciseTestPath);

    // Copy the exercise files to the code directory
    copyDirectory(exercisePath, codePath);

    // Find the main file in the code directory
    Path mainFilePath = findMainFile(codePath, language.getFileExtension());
    if (mainFilePath == null) {
      throw new IOException("Could not find main file in exercise directory: " + codePath);
    }

    // We need to write the decoded code to the main file
    Files.writeString(mainFilePath, decodedCode, StandardCharsets.UTF_8);
    log.info("Code written to main file: {}", mainFilePath);

    // Handle Go-specific setup
    if (language.getName().equals("go")) {
      setupGoModule(codePath);
    }

    // Some languages require the test files to be in a separate directory
    if (!language.getName().equals("typescript") && !language.getName().equals("java")) {
      copyDirectory(exercisePath, exerciseTestPath);
    }
  }

  private Path findMainFile(Path directory, String fileExtension) throws IOException {
    try (Stream<Path> paths = Files.walk(directory)) {
      return paths
          .filter(Files::isRegularFile)
          .filter(path -> path.toString().endsWith(fileExtension))
          .filter(path -> {
            String fileName = path.getFileName().toString();
            return !fileName.contains("test") && !fileName.contains("Test");
          })
          .findFirst()
          .orElse(null);
    }
  }

  private ExecutionResult runContainer(Language language, Path codePath, Path exerciseTestPath,
      String containerId) {
    HostConfig hostConfig = HostConfig.newHostConfig()
        .withBinds(
            Bind.parse(codePath.toString() + ":/code"),
            Bind.parse(exerciseTestPath.toString() + ":/exercise")
        )
        .withMemory(language.getDefaultMemoryLimit())
        .withCpuCount(language.getDefaultCpuLimit());

    CreateContainerResponse container = dockerClient.createContainerCmd(language.getDockerImage())
        .withHostConfig(hostConfig)
        .withName("code-execution-" + containerId)
        .withCmd("sh", "-c", language.getExecuteCommand())
        .withWorkingDir("/code")
        .exec();

    try (AutoCloseableContainer _ = new AutoCloseableContainer(container, dockerClient)) {
      dockerClient.startContainerCmd(container.getId()).exec();

      ExecutionData executionData = executeAndCollectData(container.getId(),
          language.getDefaultTimeout());
      return new ExecutionResult(executionData.exitCode(), executionData.logs().stdout(),
          executionData.logs().stderr(), 0L, 0L);
    } catch (RuntimeException e) {
      // Re-throw RuntimeExceptions (including our interrupted exception) as is
      throw e;
    } catch (Exception e) {
      log.error("Error executing code in Docker container", e);
      return new ExecutionResult(-1, "", e.getMessage(), 0L, 0L);
    } finally {
      if (container != null) {
        try {
          boolean containerExists =
              dockerClient.inspectContainerCmd(container.getId()).exec() != null;
          if (containerExists) {
            dockerClient.removeContainerCmd(container.getId()).withForce(true).exec();
            log.info("Container {} removed successfully", container.getId());
          } else {
            log.info("Container {} does not exist, skipping removal", container.getId());
          }
        } catch (NotFoundException e) {
          log.info("Container {} does not exist, skipping removal", container.getId());
        } catch (Exception e) {
          log.warn("Error removing Docker container: {}. Reason: {}", container.getId(),
              e.getMessage());
        }
      }
    }
  }

  private ExecutionData executeAndCollectData(String containerId, long timeout) {
    try {
      int exitCode = waitForContainer(containerId, timeout);
      ContainerLogs logs = collectContainerLogs(containerId, timeout);
      return new ExecutionData(exitCode, logs);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ContainerExecutionException("Container execution was interrupted", e);
    }
  }

  private record ExecutionData(int exitCode, ContainerLogs logs) {

  }

  private int waitForContainer(String containerId, long timeout) {
    return dockerClient.waitContainerCmd(containerId)
        .exec(new WaitContainerResultCallback())
        .awaitStatusCode(timeout, TimeUnit.MILLISECONDS);
  }

  private ContainerLogs collectContainerLogs(String containerId, long timeout)
      throws InterruptedException {
    ByteArrayOutputStream stdout = new ByteArrayOutputStream();
    ByteArrayOutputStream stderr = new ByteArrayOutputStream();

    LogContainerCmd logContainerCmd = dockerClient.logContainerCmd(containerId)
        .withStdOut(true)
        .withStdErr(true);

    logContainerCmd.exec(new ResultCallback.Adapter<Frame>() {
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
  }

  private record ContainerLogs(String stdout, String stderr) {

  }

  private void copyDirectory(Path source, Path target) throws IOException {
    try (Stream<Path> stream = Files.walk(source)) {
      stream.forEach(sourcePath -> {
        try {
          Path targetPath = target.resolve(source.relativize(sourcePath));
          if (Files.isDirectory(sourcePath)) {
            Files.createDirectories(targetPath);
          } else {
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
          }
        } catch (IOException e) {
          log.error("Error copying file: {} to {}", sourcePath, target, e);
        }
      });
    }
  }


  private void cleanupTempDirectory(Path tempDir) {
    int maxRetries = 5;
    long initialWaitTime = 1000; // 1 second

    for (int retry = 0; retry < maxRetries; retry++) {
      try {
        Files.walkFileTree(tempDir, new SimpleFileVisitor<>() {
          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            return deleteFileOrDirectory(file);
          }

          @Override
          public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            return deleteFileOrDirectory(dir);
          }

          private FileVisitResult deleteFileOrDirectory(Path path) {
            try {
              Files.deleteIfExists(path);
            } catch (IOException e) {
              log.warn("Unable to delete: {}. Reason: {}", path, e.getMessage());
            }
            return FileVisitResult.CONTINUE;
          }
        });

        // If we reach this point, the cleanup was successful
        log.info("Temporary directory cleaned up successfully: {}", tempDir);
        return;
      } catch (IOException e) {
        if (retry == maxRetries - 1) {
          log.error("Failed to clean up temporary directory after {} retries: {}", maxRetries, tempDir, e);
        } else {
          log.warn("Cleanup attempt {} failed, retrying after delay: {}", retry + 1, tempDir);
          try {
            Thread.sleep(initialWaitTime * (long) Math.pow(2, retry));
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("Cleanup was interrupted", ie);
            return;
          }
        }
      }
    }
  }

  private void setupGoModule(Path codePath) throws IOException {
    Path goModPath = codePath.resolve("go.mod");
    if (!Files.exists(goModPath)) {
      String moduleName = detectGoModuleName(codePath);
      String goModContent = String.format("module %s%ngo 1.23.0%n", moduleName);
      Files.writeString(goModPath, goModContent);
      log.info("Created go.mod file with module name: {}", moduleName);
    }
  }

  private String detectGoModuleName(Path codePath) {
    // Assuming the code path is in the format: <root>/exercises/go/practice/<exercise-name>
    // For example: github.com/cuervolu/cortex-exercises/exercises/go/practice/<exercise-name>
    String pathString = codePath.toString();
    int practiceIndex = pathString.lastIndexOf("/practice/");
    if (practiceIndex != -1) {
      return "github.com/cuervolu/cortex-exercises/exercises/go/practice/" +
          codePath.getFileName().toString();
    }
    // If we can't find the practice directory, return a default module name
    // This should not happen in practice!
    return "github.com/cuervolu/cortex-exercises/unknown-exercise";
  }

  public record ExecutionResult(int exitCode, String stdout, String stderr, long executionTime,
                                long memoryUsed) {

  }
}