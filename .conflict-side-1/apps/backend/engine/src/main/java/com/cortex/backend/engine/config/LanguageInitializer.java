package com.cortex.backend.engine.config;

import com.cortex.backend.core.domain.Language;
import com.cortex.backend.engine.api.LanguageRepository;
import com.cortex.backend.engine.internal.LanguageConfig;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LanguageInitializer {

  private final LanguageRepository languageRepository;

  private static final long MB = 1024 * 1024L;
  private static final long DEFAULT_CPU_LIMIT = 1L;
  private static final long DEFAULT_TIMEOUT = 30000L;

  @PostConstruct
  @Transactional
  public void initializeLanguages() {
    List<LanguageConfig> languageConfigs = Arrays.asList(
        LanguageConfig.builder()
            .name("python")
            .dockerImage("python:3.12-slim")
            .executeCommand(
                "python $(find /code -name '*.py' ! -name '*_test.py') && python -m unittest discover /code")
            .fileExtension(".py")
            .memoryLimit(128 * MB)
            .cpuLimit(DEFAULT_CPU_LIMIT)
            .timeout(DEFAULT_TIMEOUT)
            .build(),
        LanguageConfig.builder()
            .name("java")
            .dockerImage("maven:3.9.9-eclipse-temurin-21")
            .executeCommand("cd /code && mvn test -B -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn -Dsurefire.useFile=false")// Only print test results, for Debug use `.executeCommand("cd /code && mvn test")`
            .fileExtension(".java")
            .memoryLimit(512 * MB)
            .cpuLimit(DEFAULT_CPU_LIMIT)
            .timeout(60000L)
            .build(),
        LanguageConfig.builder()
            .name("typescript")
            .dockerImage("cortex-typescript-exercises:latest") // Custom image with pnpm installed
            .executeCommand(
                "cd /app/exercises/practice/{exerciseName} && pnpm install && pnpm test")
            .fileExtension(".ts")
            .memoryLimit(1024 * MB)
            .cpuLimit(DEFAULT_CPU_LIMIT)
            .timeout(180000L)
            .build(),
        LanguageConfig.builder()
            .name("rust")
            .dockerImage("rust:1.80-slim")
            .executeCommand("cd /code && cargo test")
            .fileExtension(".rs")
            .memoryLimit(256 * MB)
            .cpuLimit(DEFAULT_CPU_LIMIT)
            .timeout(DEFAULT_TIMEOUT)
            .build(),
        LanguageConfig.builder()
            .name("csharp")
            .dockerImage("mcr.microsoft.com/dotnet/sdk:8.0")
            .executeCommand("cd /code && dotnet test")
            .fileExtension(".cs")
            .memoryLimit(512 * MB)
            .cpuLimit(2L)
            .timeout(60000L)
            .build(),
        LanguageConfig.builder()
            .name("go")
            .dockerImage("golang:1.23-bookworm")
            .executeCommand("cd /code && go mod tidy && go test")
            .fileExtension(".go")
            .memoryLimit(512 * MB)
            .cpuLimit(2L)
            .timeout(30000L)
            .build()
    );

    for (LanguageConfig config : languageConfigs) {
      Language language = createLanguage(config);
      languageRepository.findByName(language.getName())
          .ifPresentOrElse(
              existingLanguage -> updateLanguage(existingLanguage, language),
              () -> languageRepository.save(language)
          );
    }
  }

  private Language createLanguage(LanguageConfig config) {
    return Language.builder()
        .name(config.getName())
        .dockerImage(config.getDockerImage())
        .executeCommand(config.getExecuteCommand())
        .compileCommand(config.getCompileCommand())
        .fileExtension(config.getFileExtension())
        .defaultMemoryLimit(config.getMemoryLimit())
        .defaultCpuLimit(config.getCpuLimit())
        .defaultTimeout(config.getTimeout())
        .createdBy(1L)
        .build();
  }

  private void updateLanguage(Language existingLanguage, Language newLanguage) {
    existingLanguage.setDockerImage(newLanguage.getDockerImage());
    existingLanguage.setExecuteCommand(newLanguage.getExecuteCommand());
    existingLanguage.setCompileCommand(newLanguage.getCompileCommand());
    existingLanguage.setFileExtension(newLanguage.getFileExtension());
    existingLanguage.setDefaultMemoryLimit(newLanguage.getDefaultMemoryLimit());
    existingLanguage.setDefaultCpuLimit(newLanguage.getDefaultCpuLimit());
    existingLanguage.setDefaultTimeout(newLanguage.getDefaultTimeout());
    languageRepository.save(existingLanguage);
  }
}

