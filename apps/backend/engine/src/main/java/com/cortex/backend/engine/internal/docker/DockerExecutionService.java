package com.cortex.backend.engine.internal.docker;

import static com.cortex.backend.engine.internal.utils.Constants.*;

import com.cortex.backend.core.domain.Language;
import com.cortex.backend.engine.api.LanguageRepository;
import com.cortex.backend.engine.api.dto.ContainerStats;
import com.cortex.backend.engine.config.AutoCloseableContainer;
import com.cortex.backend.engine.internal.environment.LanguageEnvironmentSetup;
import com.cortex.backend.engine.internal.environment.LanguageEnvironmentSetup.WorkspaceSetup;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.LogContainerCmd;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.MemoryStatsConfig;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.api.model.StreamType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
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
    long startTime = System.currentTimeMillis();
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

      return runContainer(language, workspace, containerId, startTime);
    } catch (Exception e) {
      log.error("Error during code execution setup", e);
      throw e;
    } finally {
      environmentSetup.cleanupDirectory(tempDir);
    }
  }

  private ExecutionResult runContainer(Language language, WorkspaceSetup workspace,
      String containerId, long startTime) {
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
        
        Thread.sleep(100);
        
        ContainerStats stats = getContainerStats(container.getId());
        
        int exitCode = waitForContainer(container.getId(), language.getDefaultTimeout());
        ContainerLogs logs = collectLogs(container.getId(), language.getDefaultTimeout());

        long executionTime = System.currentTimeMillis() - startTime;
        
        if (stats.memoryUsageBytes() == 0) {
          stats = getContainerStats(container.getId());
          log.info("Second attempt stats: {}", stats);
        }

        return new ExecutionResult(
            exitCode,
            logs.stdout(),
            logs.stderr(),
            executionTime,
            stats.memoryUsageBytes() / 1024  
        );
      }
    } catch (Exception e) {
      log.error("Error executing container", e);
      return new ExecutionResult(-1, "", e.getMessage(), 0L, 0L);
    }
  }


  private ContainerStats getContainerStats(String containerId) {
    final CountDownLatch latch = new CountDownLatch(1);
    final AtomicReference<Statistics> statsRef = new AtomicReference<>();

    try (StatsCmd statsCmd = dockerClient.statsCmd(containerId)) {
      statsCmd.withNoStream(false);

      statsCmd.exec(new ResultCallback.Adapter<Statistics>() {
        @Override
        public void onNext(Statistics stats) {
          if (stats != null && stats.getMemoryStats() != null &&
              stats.getMemoryStats().getUsage() != null) {
            statsRef.set(stats);
            latch.countDown();
          }
        }

        @Override
        public void onError(Throwable throwable) {
          log.error("Error getting container stats", throwable);
          latch.countDown();
        }
      });

      if (!latch.await(5, TimeUnit.SECONDS)) {
        log.warn("Timeout waiting for container stats");
        return new ContainerStats(0L, 0L);
      }

      Statistics stats = statsRef.get();
      if (stats != null && stats.getMemoryStats() != null) {
        Long usage = stats.getMemoryStats().getUsage();
        MemoryStatsConfig memStats = stats.getMemoryStats();
        Long cache = memStats.getStats() != null ?
            memStats.getStats().getCache() : Long.valueOf(0L);

        long realMemory = (usage != null ? usage : 0L) - (cache != null ? cache : 0L);
        log.info("Memory usage: {}, Cache: {}, Real memory: {}", usage, cache, realMemory);
        return new ContainerStats(realMemory, calculateCpuUsage(stats));
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.error("Thread interrupted while getting container stats", e);
    } catch (Exception e) {
      log.error("Error getting container stats", e);
    }

    return new ContainerStats(0L, 0L);
  }

  private long calculateCpuUsage(Statistics stats) {
    try {
      if (stats.getCpuStats() == null ||
          stats.getCpuStats().getCpuUsage() == null ||
          stats.getCpuStats().getCpuUsage().getTotalUsage() == null ||
          stats.getCpuStats().getSystemCpuUsage() == null) {
        return 0L;
      }

      Long totalUsage = stats.getCpuStats().getCpuUsage().getTotalUsage();
      Long systemUsage = stats.getCpuStats().getSystemCpuUsage();
      Long preTotalUsage = stats.getPreCpuStats() != null &&
          stats.getPreCpuStats().getCpuUsage() != null &&
          stats.getPreCpuStats().getCpuUsage().getTotalUsage() != null
          ? stats.getPreCpuStats().getCpuUsage().getTotalUsage()
          : 0L;
      Long preSystemUsage = stats.getPreCpuStats() != null &&
          stats.getPreCpuStats().getSystemCpuUsage() != null
          ? stats.getPreCpuStats().getSystemCpuUsage()
          : 0L;

      long cpuDelta = totalUsage - preTotalUsage;
      long systemDelta = systemUsage - preSystemUsage;

      if (systemDelta > 0 && cpuDelta > 0) {
        return (cpuDelta * 100) / systemDelta;
      }
    } catch (Exception e) {
      log.error("Error calculating CPU usage", e);
    }
    return 0L;
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

  private record ContainerLogs(String stdout, String stderr) {

  }

  public record ExecutionResult(int exitCode, String stdout, String stderr,
                                long executionTime, long memoryUsed) {

  }
}