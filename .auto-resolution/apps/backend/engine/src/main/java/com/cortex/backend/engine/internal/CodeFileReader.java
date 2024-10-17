package com.cortex.backend.engine.internal;

import java.util.Arrays;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@Component
public class CodeFileReader {

  private enum Language {
    RUST(".rs", "src", "tests"),
    TYPESCRIPT(".ts", "src", "tests"),
    GO(".go", "", ""),
    JAVA(".java", "src/main/java", "src/test/java"),
    PYTHON(".py", "", "");

    public final String extension;
    public final String srcDir;
    public final String testDir;

    Language(String extension, String srcDir, String testDir) {
      this.extension = extension;
      this.srcDir = srcDir;
      this.testDir = testDir;
    }

    public static Stream<Language> stream() {
      return Arrays.stream(Language.values());
    }
  }

  public String readTestCode(Path exercisePath) throws IOException {
    for (Language lang : Language.values()) {
      if (exercisePath.toString().contains(lang.name().toLowerCase())) {
        String testCode = readLanguageSpecificTestCode(exercisePath, lang);
        if (!testCode.isEmpty()) {
          return testCode;
        }
      }
    }

    Optional<Path> testPath = findFileInDirectory(exercisePath,
        path -> path.toString().contains("test") || path.toString().contains("Test"));

    if (testPath.isEmpty()) {
      testPath = findLanguageSpecificTestFile(exercisePath, Language.JAVA);
    }

    return testPath.map(this::readFileContent).orElse("");
  }

  public String readInitialCode(Path exercisePath) throws IOException {
    for (Language lang : Language.values()) {
      if (exercisePath.toString().contains(lang.name().toLowerCase())) {
        String initialCode = readLanguageSpecificInitialCode(exercisePath, lang);
        if (!initialCode.isEmpty()) {
          return initialCode;
        }
      }
    }

    Optional<Path> codePath = findInitialCodeFile(exercisePath);

    if (codePath.isEmpty()) {
      codePath = findLanguageSpecificInitialFile(exercisePath, Language.JAVA);
    }

    return codePath.map(this::readFileContent).orElse("");
  }

  private String readLanguageSpecificTestCode(Path exercisePath, Language lang) throws IOException {
    Path testPath = exercisePath.resolve(lang.testDir);
    if (Files.exists(testPath)) {
      Optional<Path> testFilePath = findFileInDirectory(testPath,
          path -> path.toString().endsWith(lang.extension) ||
              path.toString().endsWith(".test" + lang.extension));
      return testFilePath.map(this::readFileContent).orElse("");
    }
    return "";
  }

  private String readLanguageSpecificInitialCode(Path exercisePath, Language lang)
      throws IOException {
    Path srcPath = exercisePath.resolve(lang.srcDir);
    if (Files.exists(srcPath)) {
      Optional<Path> codePath = findFileInDirectory(srcPath,
          path -> path.toString().endsWith(lang.extension));
      return codePath.map(this::readFileContent).orElse("");
    }
    return "";
  }

  private Optional<Path> findLanguageSpecificTestFile(Path exercisePath, Language lang)
      throws IOException {
    Path testPath = exercisePath.resolve(lang.testDir);
    if (Files.exists(testPath)) {
      return findFileInDirectory(testPath,
          path -> path.toString().endsWith("Test" + lang.extension));
    }
    return Optional.empty();
  }

  private Optional<Path> findLanguageSpecificInitialFile(Path exercisePath, Language lang)
      throws IOException {
    Path srcPath = exercisePath.resolve(lang.srcDir);
    if (Files.exists(srcPath)) {
      return findFileInDirectory(srcPath,
          path -> !path.toString().contains("test") && !path.toString().contains("Test") &&
              path.toString().endsWith(lang.extension));
    }
    return Optional.empty();
  }

  private Optional<Path> findInitialCodeFile(Path exercisePath) throws IOException {
    return findFileInDirectory(exercisePath, this::isInitialCodeFile);
  }

  private boolean isInitialCodeFile(Path path) {
    String pathString = path.toString();
    return !pathString.contains("test") &&
        !pathString.contains("Test") &&
        Language.stream().anyMatch(lang -> pathString.endsWith(lang.extension));
  }

  public String readFileContent(Path path) {
    if (Files.isDirectory(path)) {
      log.warn("Attempted to read a directory as a file: {}", path);
      return "";
    }
    try {
      return Files.readString(path);
    } catch (IOException e) {
      log.error("Error reading file: {}", path, e);
      return "";
    }
  }

  private Optional<Path> findFileInDirectory(Path directory, Predicate<Path> predicate)
      throws IOException {
    try (var paths = Files.list(directory)) {
      return paths.filter(predicate).findFirst();
    }
  }
}