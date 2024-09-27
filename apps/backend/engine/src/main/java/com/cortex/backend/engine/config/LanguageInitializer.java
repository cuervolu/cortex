package com.cortex.backend.engine.config;

import com.cortex.backend.core.domain.Language;
import com.cortex.backend.engine.api.LanguageRepository;
import com.cortex.backend.engine.internal.LanguageConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LanguageInitializer {

  private final LanguageRepository languageRepository;

  private static final long MB = 1024 * 1024L;
  private static final long DEFAULT_CPU_LIMIT = 1L;
  private static final long DEFAULT_TIMEOUT = 5000L;

  @PostConstruct
  @Transactional
  public void initializeLanguages() {
    List<LanguageConfig> languageConfigs = Arrays.asList(
        LanguageConfig.builder()
            .name("python")
            .dockerImage("python:3.12-slim")
            .executeCommand("python {fileName}")
            .fileExtension(".py")
            .memoryLimit(128 * MB)
            .cpuLimit(DEFAULT_CPU_LIMIT)
            .timeout(DEFAULT_TIMEOUT)
            .build(),
        LanguageConfig.builder()
            .name("java")
            .dockerImage("eclipse-temurin:21")
            .executeCommand("java {fileName}")
            .compileCommand("javac {fileName}")
            .fileExtension(".java")
            .memoryLimit(256 * MB)
            .cpuLimit(DEFAULT_CPU_LIMIT)
            .timeout(10000L)
            .build(),
        LanguageConfig.builder()
            .name("javascript")
            .dockerImage("node:20-alpine3.19")
            .executeCommand("node {fileName}")
            .fileExtension(".js")
            .memoryLimit(128 * MB)
            .cpuLimit(DEFAULT_CPU_LIMIT)
            .timeout(DEFAULT_TIMEOUT)
            .build(),
        LanguageConfig.builder()
            .name("rust")
            .dockerImage("rust:1.80-slim")
            .executeCommand("rustc {fileName} && ./{fileNameWithoutExtension}")
            .fileExtension(".rs")
            .memoryLimit(256 * MB)
            .cpuLimit(DEFAULT_CPU_LIMIT)
            .timeout(15000L)
            .build(),
        LanguageConfig.builder()
            .name("csharp")
            .dockerImage("mcr.microsoft.com/dotnet/sdk:8.0")
            .executeCommand("dotnet new console -o . && mv {fileName} Program.cs && dotnet run")
            .fileExtension(".cs")
            .memoryLimit(512 * MB)
            .cpuLimit(2L)
            .timeout(30000L)
            .build(),
        LanguageConfig.builder()
            .name("go")
            .dockerImage("golang:1.22-bookworm")
            .executeCommand("go run {fileName}")
            .fileExtension(".go")
            .memoryLimit(256 * MB)
            .cpuLimit(DEFAULT_CPU_LIMIT)
            .timeout(10000L)
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

