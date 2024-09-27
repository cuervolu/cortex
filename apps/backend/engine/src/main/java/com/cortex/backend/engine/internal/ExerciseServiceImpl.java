package com.cortex.backend.engine.internal;

import com.cortex.backend.core.common.SlugUtils;
import com.cortex.backend.core.common.exception.ExerciseCreationException;
import com.cortex.backend.core.common.exception.ExerciseReadException;
import com.cortex.backend.core.domain.Exercise;
import com.cortex.backend.education.lesson.internal.LessonRepository;
import com.cortex.backend.engine.api.ExerciseRepository;
import com.cortex.backend.engine.api.ExerciseService;
import com.cortex.backend.engine.api.dto.CreateExercise;
import com.cortex.backend.engine.api.dto.ExerciseDetailsResponse;
import com.cortex.backend.engine.api.dto.ExerciseResponse;
import com.cortex.backend.engine.api.dto.UpdateExercise;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {

  private final ExerciseRepository exerciseRepository;
  private final LessonRepository lessonRepository;
  private final ExerciseMapper exerciseMapper;
  private final SlugUtils slugUtils;

  private static final String JAVA_FILE_EXTENSION = ".java";
  private static final String RUST_FILE_EXTENSION = ".rs";
  private static final String PYTHON_FILE_EXTENSION = ".py";
  private static final String GO_FILE_EXTENSION = ".go";
  private static final String TYPESCRIPT_FILE_EXTENSION = ".ts";


  @Value("${github.exercises.local-path}")
  private String localRepoPath;

  @Override
  @Transactional
  public ExerciseResponse createExercise(CreateExercise createExerciseDTO) {
    Exercise exercise = exerciseMapper.createExerciseDtoToExercise(createExerciseDTO);
    Exercise savedExercise = exerciseRepository.save(exercise);
    return exerciseMapper.exerciseToExerciseResponse(savedExercise);
  }

  @Override
  @Transactional
  public ExerciseResponse updateExercise(Long id, UpdateExercise updateExerciseDTO) {
    Exercise exercise = exerciseRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));
    exerciseMapper.updateExerciseFromDto(updateExerciseDTO, exercise);
    Exercise updatedExercise = exerciseRepository.save(exercise);
    return exerciseMapper.exerciseToExerciseResponse(updatedExercise);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<ExerciseResponse> getExercise(Long id) {
    return exerciseRepository.findById(id)
        .map(exerciseMapper::exerciseToExerciseResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<ExerciseResponse> getExerciseBySlug(String slug) {
    return exerciseRepository.findBySlug(slug)
        .map(exerciseMapper::exerciseToExerciseResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ExerciseResponse> getAllExercises() {
    return StreamSupport.stream(exerciseRepository.findAll().spliterator(), false)
        .map(exerciseMapper::exerciseToExerciseResponse)
        .toList();
  }

  @Override
  @Transactional
  public void deleteExercise(Long id) {
    exerciseRepository.deleteById(id);
  }

  @Override
  public boolean isExerciseRepositoryEmpty() {
    return exerciseRepository.count() == 0;
  }


  @Override
  @Transactional
  public void updateOrCreateExercise(String exerciseName, String githubPath, String instructions,
      String hints) {
    log.info("Updating or creating exercise: {}", exerciseName);
    try {
      Exercise existingExercise = exerciseRepository.findByGithubPath(githubPath).orElse(null);
      if (existingExercise == null) {
        log.info("Creating new exercise: {}", exerciseName);
        CreateExercise createExercise = CreateExercise.builder()
            .title(exerciseName)
            .githubPath(githubPath)
            .points(0)
            .lessonId(1L) // TODO: Change this to a real lesson ID
            .instructions(instructions)
            .hints(hints)
            .lastGithubSync(LocalDateTime.now())
            .build();
        Exercise newExercise = exerciseMapper.createExerciseDtoToExercise(createExercise);

        // Generate a unique slug for the new exercise
        String slug = slugUtils.generateUniqueSlug(exerciseName,
            s -> exerciseRepository.findBySlug(s).isPresent());
        newExercise.setSlug(slug);

        exerciseRepository.save(newExercise);
        log.info("New exercise created with ID: {} and slug: {}", newExercise.getId(),
            newExercise.getSlug());
      } else {
        log.info("Updating existing exercise: {}", exerciseName);
        existingExercise.setLastGithubSync(LocalDateTime.now());
        existingExercise.setInstructions(instructions);
        existingExercise.setHints(hints);

        // Check if slug needs to be updated
        if (existingExercise.getSlug() == null || existingExercise.getSlug().isEmpty()) {
          String slug = slugUtils.generateUniqueSlug(exerciseName,
              s -> !s.equals(existingExercise.getSlug()) && exerciseRepository.findBySlug(s)
                  .isPresent());
          existingExercise.setSlug(slug);
        }

        exerciseRepository.save(existingExercise);
        log.info("Exercise updated with ID: {} and slug: {}", existingExercise.getId(),
            existingExercise.getSlug());
      }
    } catch (Exception e) {
      log.error("Error updating or creating exercise: {}", exerciseName, e);
      throw new ExerciseCreationException("Failed to update or create exercise", e);
    }
  }


  @Override
  @Transactional(readOnly = true)
  public ExerciseDetailsResponse getExerciseDetails(Long id) {
    Exercise exercise = exerciseRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Exercise not found with id: " + id));

    Path exercisePath = Paths.get(localRepoPath, exercise.getGithubPath());

    try {
      String initialCode = readInitialCode(exercisePath);
      String testCode = readTestCode(exercisePath);

      return ExerciseDetailsResponse.builder()
          .id(exercise.getId())
          .title(exercise.getTitle())
          .instructions(exercise.getInstructions())
          .hints(exercise.getHints())
          .initialCode(initialCode)
          .testCode(testCode)
          .build();
    } catch (IOException e) {
      log.error("Error reading exercise files for exercise id: {}", id, e);
      throw new ExerciseReadException("Failed to read exercise files", e);
    }
  }

  private String readInitialCode(Path exercisePath) throws IOException {
    // For Rust, look in the 'src' directory first
    if (exercisePath.toString().contains("rust")) {
      Path rustSrcPath = exercisePath.resolve("src");
      if (Files.exists(rustSrcPath)) {
        Optional<Path> rustCodePath = findFileInDirectory(rustSrcPath,
            path -> path.toString().endsWith(RUST_FILE_EXTENSION));
        if (rustCodePath.isPresent()) {
          return readFileContent(rustCodePath.get());
        }
      }
    }

    // For TypeScript, look in the 'src' directory first
    if (exercisePath.toString().contains("typescript")) {
      Path tsSrcPath = exercisePath.resolve("src");
      if (Files.exists(tsSrcPath)) {
        Optional<Path> tsCodePath = findFileInDirectory(tsSrcPath,
            path -> path.toString().endsWith(TYPESCRIPT_FILE_EXTENSION));
        if (tsCodePath.isPresent()) {
          return readFileContent(tsCodePath.get());
        }
      }
    }

    // For other languages or if not found in specific directories
    Optional<Path> codePath = findFileInDirectory(exercisePath,
        path -> !path.toString().contains("test") && !path.toString().contains("Test") &&
            (path.toString().endsWith(GO_FILE_EXTENSION) ||
                path.toString().endsWith(JAVA_FILE_EXTENSION) ||
                path.toString().endsWith(PYTHON_FILE_EXTENSION) ||
                path.toString().endsWith(RUST_FILE_EXTENSION) ||
                path.toString().endsWith(TYPESCRIPT_FILE_EXTENSION)));

    // If we don't find it in the root, we look in the 'src' directory (for Java)
    if (codePath.isEmpty()) {
      Path srcPath = exercisePath.resolve("src");
      if (Files.exists(srcPath)) {
        codePath = findFileInDirectory(srcPath,
            path -> !path.toString().contains("test") && !path.toString().contains("Test") &&
                path.toString().endsWith(JAVA_FILE_EXTENSION));
      }
    }

    // If we still can't find it, we look in 'src/main/java' (specific for Java)
    if (codePath.isEmpty()) {
      Path javaMainPath = exercisePath.resolve("src").resolve("main").resolve("java");
      if (Files.exists(javaMainPath)) {
        codePath = findFileInDirectory(javaMainPath,
            path -> path.toString().endsWith(JAVA_FILE_EXTENSION));
      }
    }

    return codePath.map(this::readFileContent).orElse("");
  }

  private String readTestCode(Path exercisePath) throws IOException {
    // For Rust, look in the 'tests' directory first
    if (exercisePath.toString().contains("rust")) {
      Path rustTestsPath = exercisePath.resolve("tests");
      if (Files.exists(rustTestsPath)) {
        Optional<Path> rustTestPath = findFileInDirectory(rustTestsPath,
            path -> path.toString().endsWith(RUST_FILE_EXTENSION));
        if (rustTestPath.isPresent()) {
          return readFileContent(rustTestPath.get());
        }
      }
    }

    // For TypeScript, look in the 'tests' directory first
    if (exercisePath.toString().contains("typescript")) {
      Path tsTestsPath = exercisePath.resolve("tests");
      if (Files.exists(tsTestsPath)) {
        Optional<Path> tsTestPath = findFileInDirectory(tsTestsPath,
            path -> path.toString().endsWith(".test" + TYPESCRIPT_FILE_EXTENSION));
        if (tsTestPath.isPresent()) {
          return readFileContent(tsTestPath.get());
        }
      }
    }

    // For other languages or if not found in specific directories
    Optional<Path> testPath = findFileInDirectory(exercisePath,
        path -> path.toString().contains("test") || path.toString().contains("Test"));

    // If we still can't find it, we search in 'src/test/java' (specific for Java)
    if (testPath.isEmpty()) {
      Path javaTestPath = exercisePath.resolve("src").resolve("test").resolve("java");
      if (Files.exists(javaTestPath)) {
        testPath = findFileInDirectory(javaTestPath, path -> path.toString().endsWith("Test.java"));
      }
    }

    return testPath.map(this::readFileContent).orElse("");
  }

  private Optional<Path> findFileInDirectory(Path directory,
      java.util.function.Predicate<Path> predicate) throws IOException {
    try (var paths = Files.list(directory)) {
      return paths.filter(predicate).findFirst();
    }
  }

  private String readFileContent(Path path) {
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

  @Override
  public boolean areLessonsAvailable() {
    return lessonRepository.count() > 0;
  }

}