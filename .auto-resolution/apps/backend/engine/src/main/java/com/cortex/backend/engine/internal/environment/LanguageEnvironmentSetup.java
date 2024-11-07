package com.cortex.backend.engine.internal.environment;

import static com.cortex.backend.engine.internal.utils.Constants.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import com.cortex.backend.core.domain.Language;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LanguageEnvironmentSetup {

  public record WorkspaceSetup(Path workspaceDir, Path projectRoot) {

  }

  public WorkspaceSetup setupWorkspace(Path tempDir, Path exercisePath, String decodedCode, Language language)
      throws IOException {
    // Crear y configurar el directorio de trabajo
    Path workspaceDir = tempDir.resolve("workspace");
    Files.createDirectories(workspaceDir);

    log.info("Setting up workspace at: {}", workspaceDir);
    log.info("Copying from exercise path: {}", exercisePath);

    // Copy exercise files
    copyDirectoryStructure(exercisePath, workspaceDir);

    // Verificar que los archivos se copiaron
    try (Stream<Path> paths = Files.walk(workspaceDir)) {
      log.info("Files in workspace after copy:");
      paths.forEach(p -> log.info("  {}", p));
    }

    // Configuración específica por lenguaje y obtener el directorio raíz del proyecto
    Path projectRoot = switch (language.getName()) {
      case "java" -> setupJavaEnvironment(workspaceDir);
      case "typescript" -> setupTypeScriptEnvironment(exercisePath, workspaceDir);
      case "rust" -> setupRustEnvironment(workspaceDir);
      case "go" -> setupGoEnvironment(workspaceDir);
      default -> workspaceDir;
    };

    // Escribir el código en el archivo principal
    updateMainFile(workspaceDir, decodedCode, language);

    return new WorkspaceSetup(workspaceDir, projectRoot);
  }

  private void copyDirectoryStructure(Path source, Path target) throws IOException {
    log.info("Copying directory structure from {} to {}", source, target);

    Files.walkFileTree(source, new SimpleFileVisitor<>() {
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
          throws IOException {
        Path relativePath = source.relativize(dir);
        Path targetDir = target.resolve(relativePath);
        log.debug("Creating directory: {}", targetDir);
        Files.createDirectories(targetDir);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
          throws IOException {
        Path relativePath = source.relativize(file);
        Path targetFile = target.resolve(relativePath);
        log.debug("Copying file: {} to {}", file, targetFile);
        Files.createDirectories(targetFile.getParent());
        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFileFailed(Path file, IOException exc) {
        log.error("Failed to process file: {}", file, exc);
        return FileVisitResult.CONTINUE;
      }
    });

    // Verificar que los archivos se copiaron correctamente
    if (!Files.exists(target) || !Files.isDirectory(target)) {
      throw new IOException("Failed to create target directory: " + target);
    }

    try (Stream<Path> paths = Files.walk(target)) {
      if (paths.findAny().isEmpty()) {
        throw new IOException("Target directory is empty after copy: " + target);
      }
    }
  }


  private Path setupJavaEnvironment(Path workspaceDir) throws IOException {
    log.info("Setting up Java environment in: {}", workspaceDir);

    // Buscar el directorio que contiene el pom.xml
    Optional<Path> pomPath = findFile(workspaceDir, "pom.xml");
    if (pomPath.isEmpty()) {
      log.error("No pom.xml found in workspace: {}", workspaceDir);
      // Listar todos los archivos en el workspace para debug
      try (Stream<Path> paths = Files.walk(workspaceDir)) {
        log.info("Files in workspace:");
        paths.forEach(p -> log.info("  {}", p));
      }
      throw new IOException("Missing pom.xml in Java project");
    }

    Path projectRoot = pomPath.get().getParent();
    log.info("Found Java project root at: {}", projectRoot);
    return projectRoot;
  }


  private Optional<Path> findFile(Path directory, String fileName) throws IOException {
    log.debug("Searching for {} in {}", fileName, directory);
    try (Stream<Path> paths = Files.walk(directory)) {
      Optional<Path> found = paths
          .filter(Files::isRegularFile)
          .filter(path -> path.getFileName().toString().equals(fileName))
          .findFirst();

      found.ifPresentOrElse(
          path -> log.debug("Found {} at {}", fileName, path),
          () -> log.debug("File {} not found in {}", fileName, directory)
      );

      return found;
    }
  }

  private void updateMainFile(Path workspaceDir, String decodedCode, Language language)
      throws IOException {
    Path mainFile = findMainFile(workspaceDir, language);
    if (mainFile == null) {
      throw new IOException("Could not find main file for " + language.getName());
    }
    Files.writeString(mainFile, decodedCode, StandardCharsets.UTF_8);
    Files.setPosixFilePermissions(mainFile,
        PosixFilePermissions.fromString("rw-r--r--"));
  }

  private Path findMainFile(Path directory, Language language) throws IOException {
    String extension = language.getFileExtension();
    Path sourceDir = directory;

    // Ajustar el directorio de búsqueda según el lenguaje
    if (language.getName().equals("java")) {
      sourceDir = directory.resolve(JAVA_SRC_DIR);
    } else if (language.getName().equals("rust")) {
      sourceDir = directory.resolve(RUST_SRC_DIR);
    }

    try (Stream<Path> paths = Files.walk(sourceDir)) {
      return paths
          .filter(Files::isRegularFile)
          .filter(path -> path.toString().endsWith(extension))
          .filter(path -> !path.getFileName().toString().toLowerCase().contains("test"))
          .findFirst()
          .orElse(null);
    }
  }

  private Path setupTypeScriptEnvironment(Path originalPath, Path workspaceDir) throws IOException {
    // Copiar archivos de configuración del directorio raíz de TypeScript
    Path tsRoot = originalPath.getParent().getParent();
    copyConfigFile(tsRoot, workspaceDir, PACKAGE_JSON);
    copyConfigFile(tsRoot, workspaceDir, PNPM_LOCK);
    copyConfigFile(tsRoot, workspaceDir, TSCONFIG_JSON);
    copyConfigFile(tsRoot, workspaceDir, VITEST_CONFIG);
    copyConfigFile(tsRoot, workspaceDir, BIOME_JSON);

    // Find the directory containing package.json for the specific exercise
    Optional<Path> packageJsonPath = findFile(workspaceDir, "package.json");
    if (packageJsonPath.isEmpty()) {
      throw new IOException("Missing package.json in TypeScript project");
    }
    return packageJsonPath.get().getParent();
  }

  private Path setupRustEnvironment(Path workspaceDir) throws IOException {
    // Find the directory containing Cargo.toml
    Optional<Path> cargoPath = findFile(workspaceDir, "Cargo.toml");
    if (cargoPath.isEmpty()) {
      throw new IOException("Missing Cargo.toml in Rust project");
    }
    return cargoPath.get().getParent();
  }

  private Path setupGoEnvironment(Path workspaceDir) throws IOException {
    // Find the directory containing go.mod
    Optional<Path> goModPath = findFile(workspaceDir, "go.mod");
    if (goModPath.isEmpty()) {
      throw new IOException("Missing go.mod in Go project");
    }
    return goModPath.get().getParent();
  }

  private void copyConfigFile(Path source, Path target, String fileName) throws IOException {
    Path sourceFile = source.resolve(fileName);
    if (Files.exists(sourceFile)) {
      Files.copy(sourceFile, target.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
    }
  }

  public void cleanupDirectory(Path directory) {
     log.info("Cleaning up directory: {}", directory);
//    try {
//      Files.walk(directory)
//          .sorted(Comparator.reverseOrder())
//          .forEach(path -> {
//            try {
//              Files.deleteIfExists(path);
//            } catch (IOException e) {
//              log.warn("Failed to delete {}: {}", path, e.getMessage());
//            }
//          });
//    } catch (IOException e) {
//      log.error("Error cleaning up directory {}", directory, e);
//    }
  }
}