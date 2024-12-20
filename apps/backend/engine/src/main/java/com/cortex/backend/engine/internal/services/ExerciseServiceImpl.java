package com.cortex.backend.engine.internal.services;

import com.cortex.backend.auth.config.ApplicationAuditAware;
import com.cortex.backend.core.common.PageResponse;
import com.cortex.backend.core.common.exception.ExerciseCreationException;
import com.cortex.backend.core.common.exception.ExerciseReadException;
import com.cortex.backend.core.domain.Exercise;
import com.cortex.backend.core.domain.ExerciseDifficulty;
import com.cortex.backend.core.domain.ExerciseStatus;
import com.cortex.backend.core.domain.Lesson;
import com.cortex.backend.education.lesson.api.LessonRepository;
import com.cortex.backend.engine.api.ExerciseRepository;
import com.cortex.backend.engine.api.ExerciseService;
import com.cortex.backend.engine.api.dto.BulkStatusUpdateRequest;
import com.cortex.backend.engine.api.dto.CreateExercise;
import com.cortex.backend.engine.api.dto.ExerciseDetailsResponse;
import com.cortex.backend.engine.api.dto.ExerciseResponse;
import com.cortex.backend.engine.api.dto.ExerciseStats;
import com.cortex.backend.engine.api.dto.UpdateExercise;
import com.cortex.backend.engine.internal.CodeFileReader;
import com.cortex.backend.engine.internal.ContentData;
import com.cortex.backend.engine.internal.ExerciseConfig;
import com.cortex.backend.engine.internal.mappers.ExerciseMapper;
import com.cortex.backend.user.api.UserService;
import com.cortex.backend.user.api.dto.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
  private final UserService userService;
  private final CodeFileReader codeFileReader;


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
  public PageResponse<ExerciseResponse> getAllExercises(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

    Page<Exercise> exercises = exerciseRepository.findAllExercisesWithPublishedLessons(pageable);

    List<ExerciseResponse> response = exercises.stream()
        .map(exerciseMapper::exerciseToExerciseResponse).toList();

    return new PageResponse<>(response, exercises.getNumber(), exercises.getSize(),
        exercises.getTotalElements(), exercises.getTotalPages(), exercises.isFirst(),
        exercises.isLast());


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
  public void updateOrCreateExercise(
      String exerciseName,
      String githubPath,
      String instructions,
      String hints,
      String slug,
      String language,
      ExerciseConfig config,
      Set<Long> prerequisites,
      Set<String> tags) {

    log.info("Updating or creating exercise: {}", exerciseName);
    try {
      Optional<UserResponse> user = userService.getUserByUsername(
          config.getContent().getCreator().toLowerCase());
      Optional<Lesson> lesson = lessonRepository.findBySlug(
          config.getContent().getLessonSlug());

      Exercise exercise = exerciseRepository.findByGithubPath(githubPath)
          .orElse(Exercise.builder()
              .slug(slug)
              .githubPath(githubPath)
              .lastGithubSync(LocalDateTime.now())
              .build());

      ContentData content = config.getContent();
      exercise.setTitle(config.getTitle());
      exercise.setPoints(content.getPoints());
      exercise.setInstructions(instructions);
      exercise.setHints(hints);
      exercise.setDisplayOrder(content.getDisplayOrder());
      exercise.setTags(tags);
      exercise.setPrerequisiteExercises(prerequisites);
      exercise.setDifficulty(
          ExerciseDifficulty.valueOf(content.getDifficulty().toUpperCase())
      );
      exercise.setEstimatedTimeMinutes(content.getEstimatedTimeMinutes());

      if (user.isEmpty() || lesson.isEmpty()) {
        exercise.setStatus(ExerciseStatus.PENDING_REVIEW);
        exercise.setPendingCreator(content.getCreator());
        exercise.setPendingLessonSlug(content.getLessonSlug());
        // TODO: Implement notification system for administrators
      } else {
        exercise.setStatus(ExerciseStatus.PUBLISHED);
        exercise.setLesson(lesson.get());
        exercise.setPendingCreator(null);
        exercise.setPendingLessonSlug(null);
        ApplicationAuditAware.setCurrentAuditor(user.get().getId());
      }

      exerciseRepository.save(exercise);

    } catch (Exception e) {
      log.error("Error processing exercise: {}", exerciseName, e);
      throw new ExerciseCreationException("Failed to update or create exercise", e);
    } finally {
      ApplicationAuditAware.clearCurrentAuditor();
    }
  }

  @Override
  @Transactional(readOnly = true)
  public ExerciseDetailsResponse getExerciseDetails(Long id) {
    Exercise exercise = exerciseRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Exercise not found with id: " + id));
    Path exercisePath = Paths.get(localRepoPath, exercise.getGithubPath());
    try {
      String language = determineLanguage(exercise.getGithubPath());
      String initialCode;
      String testCode;
      String fileName;

      if ("go".equals(language)) {
        fileName = findFileByExtension(exercisePath, ".go", false);
        initialCode = codeFileReader.readFileContent(exercisePath.resolve(fileName));
        String testFileName = findFileByExtension(exercisePath, ".go", true);
        testCode = codeFileReader.readFileContent(exercisePath.resolve(testFileName));
      } else if ("python".equals(language)) {
        fileName = findFileByExtension(exercisePath, ".py", false);
        initialCode = codeFileReader.readFileContent(exercisePath.resolve(fileName));
        String testFileName = findFileByExtension(exercisePath, ".py", true);
        testCode = codeFileReader.readFileContent(exercisePath.resolve(testFileName));
      } else {
        initialCode = codeFileReader.readInitialCode(exercisePath);
        testCode = codeFileReader.readTestCode(exercisePath);
        fileName = determineFileName(exercisePath, language);
      }

      return ExerciseDetailsResponse.builder()
          .id(exercise.getId())
          .title(exercise.getTitle())
          .instructions(exercise.getInstructions())
          .hints(exercise.getHints())
          .initialCode(initialCode)
          .testCode(testCode)
          .lessonName(exercise.getLesson().getName())
          .fileName(fileName)
          .language(language)
          .slug(exercise.getSlug())
          .build();
    } catch (IOException e) {
      log.error("Error reading exercise files for exercise id: {}", id, e);
      throw new ExerciseReadException("Failed to read exercise files", e);
    }
  }

  private String findFileByExtension(Path exercisePath, String extension, boolean isTestFile) throws IOException {
    try (Stream<Path> paths = Files.walk(exercisePath)) {
      return paths
          .filter(Files::isRegularFile)
          .filter(path -> path.toString().endsWith(extension))
          .filter(path -> {
            String fileName = path.getFileName().toString().toLowerCase();
            if (isTestFile) {
              return fileName.startsWith("test_") || fileName.endsWith("_test" + extension);
            } else {
              return !fileName.startsWith("test_") && !fileName.endsWith("_test" + extension);
            }
          })
          .findFirst()
          .map(Path::getFileName)
          .map(Path::toString)
          .orElseThrow(() -> new IOException("File with extension " + extension + " not found in " + exercisePath));
    }
  }

  @Override
  public boolean areLessonsAvailable() {
    return lessonRepository.count() > 0;
  }


  @Override
  @Transactional(readOnly = true)
  public PageResponse<ExerciseResponse> getAllExercises(int page, int size,
      ExerciseStatus status, String difficulty, List<String> tags) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("displayOrder").ascending());

    Specification<Exercise> spec = Specification.where(null);

    if (status != null) {
      spec = spec.and((root, query, cb) ->
          cb.equal(root.get("status"), status));
    } else {
      // Por defecto, solo mostrar ejercicios publicados
      spec = spec.and((root, query, cb) ->
          cb.equal(root.get("status"), ExerciseStatus.PUBLISHED));
    }

    if (difficulty != null) {
      spec = spec.and((root, query, cb) ->
          cb.equal(root.get("difficulty"), ExerciseDifficulty.valueOf(difficulty.toUpperCase())));
    }

    if (tags != null && !tags.isEmpty()) {
      spec = spec.and((root, query, cb) ->
          root.join("tags").in(tags));
    }

    Page<Exercise> exercises = exerciseRepository.findAll(spec, pageable);
    List<ExerciseResponse> response = exercises.stream()
        .map(exerciseMapper::exerciseToExerciseResponse)
        .toList();

    return new PageResponse<>(
        response,
        exercises.getNumber(),
        exercises.getSize(),
        exercises.getTotalElements(),
        exercises.getTotalPages(),
        exercises.isFirst(),
        exercises.isLast()
    );
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<ExerciseResponse> getExercisesByStatus(int page, int size, ExerciseStatus status) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<Exercise> exercises = exerciseRepository.findByStatus(status, pageable);

    List<ExerciseResponse> response = exercises.stream()
        .map(exerciseMapper::exerciseToExerciseResponse)
        .toList();

    return new PageResponse<>(response, exercises.getNumber(), exercises.getSize(),
        exercises.getTotalElements(), exercises.getTotalPages(),
        exercises.isFirst(), exercises.isLast());
  }

  @Override
  @Transactional
  public ExerciseResponse updateExerciseStatus(Long id, ExerciseStatus status, String reviewNotes) {
    Exercise exercise = exerciseRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

    if (status == ExerciseStatus.PUBLISHED && exercise.getLesson() == null) {
      throw new IllegalStateException("Cannot publish exercise without assigned lesson");
    }

    exercise.setStatus(status);
    if (reviewNotes != null) {
      exercise.setReviewNotes(reviewNotes);
    }

    Exercise savedExercise = exerciseRepository.save(exercise);
    return exerciseMapper.exerciseToExerciseResponse(savedExercise);
  }

  @Override
  @Transactional
  public ExerciseResponse assignLessonToExercise(Long id, Long lessonId) {
    Exercise exercise = exerciseRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

    Lesson lesson = lessonRepository.findById(lessonId)
        .orElseThrow(() -> new EntityNotFoundException("Lesson not found"));

    exercise.setLesson(lesson);
    exercise.setPendingLessonSlug(null);

    if (exercise.getStatus() == ExerciseStatus.PENDING_REVIEW &&
        exercise.getPendingCreator() == null) {
      exercise.setStatus(ExerciseStatus.PUBLISHED);
    }

    Exercise savedExercise = exerciseRepository.save(exercise);
    return exerciseMapper.exerciseToExerciseResponse(savedExercise);
  }

  @Override
  @Transactional
  public void updateExerciseOrder(Long id, Integer newOrder) {
    Exercise exercise = exerciseRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

    Integer oldOrder = exercise.getDisplayOrder();
    Long lessonId = exercise.getLesson().getId();

    if (newOrder > oldOrder) {
      exerciseRepository.decrementOrdersInRange(lessonId, oldOrder + 1, newOrder);
    } else {
      exerciseRepository.incrementOrdersInRange(lessonId, newOrder, oldOrder - 1);
    }

    exercise.setDisplayOrder(newOrder);
    exerciseRepository.save(exercise);
  }

  @Override
  @Transactional
  public void deprecateExercise(Long id) {
    Exercise exercise = exerciseRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Exercise not found"));

    exercise.setStatus(ExerciseStatus.DEPRECATED);
    exerciseRepository.save(exercise);
  }

//  @Override
//  public void forceSyncExercises() {
//    log.info("Starting forced synchronization of exercises");
//    githubSyncService.syncExercises();
//  }

  @Override
  @Transactional(readOnly = true)
  public ExerciseStats getExerciseStatistics() {
    return ExerciseStats.builder()
        .totalExercises(exerciseRepository.count())
        .publishedExercises(exerciseRepository.countByStatus(ExerciseStatus.PUBLISHED))
        .pendingReviewExercises(exerciseRepository.countByStatus(ExerciseStatus.PENDING_REVIEW))
        .deprecatedExercises(exerciseRepository.countByStatus(ExerciseStatus.DEPRECATED))
        .exercisesByDifficulty(exerciseRepository.countByDifficulty())
        .exercisesByTag(exerciseRepository.countByTags())
        .averagePoints(exerciseRepository.getAveragePoints())
        .averageCompletionRate(exerciseRepository.getAverageCompletionRate())
        .build();
  }

  @Override
  @Transactional
  public void bulkUpdateExerciseStatus(BulkStatusUpdateRequest request) {
    List<Exercise> exercises = exerciseRepository.findAllById(request.getExerciseIds());

    for (Exercise exercise : exercises) {
      if (request.getNewStatus() == ExerciseStatus.PUBLISHED &&
          exercise.getLesson() == null) {
        log.warn("Skipping exercise {} - cannot publish without lesson", exercise.getId());
        continue;
      }

      exercise.setStatus(request.getNewStatus());
      if (request.getReviewNotes() != null) {
        exercise.setReviewNotes(request.getReviewNotes());
      }
    }

    exerciseRepository.saveAll(exercises);
  }



  private Lesson getLessonBySlug(String lessonSlug) {
    return lessonRepository.findBySlug(lessonSlug)
        .orElseThrow(() -> new IllegalArgumentException("Lesson not found with id: " + lessonSlug));
  }

  private String determineLanguage(String githubPath) {
    String[] parts = githubPath.split("/");
    if (parts.length > 1) {
      return parts[1];
    }
    return "unknown";
  }

  private String determineFileName(Path exercisePath, String language) throws IOException {
    try (Stream<Path> paths = Files.walk(exercisePath)) {
      return paths
          .filter(Files::isRegularFile)
          .map(Path::getFileName)
          .map(Path::toString)
          .filter(name -> name.endsWith(getFileExtension(language)))
          .filter(name -> !name.contains("test") && !name.contains("Test"))
          .findFirst()
          .orElse("main" + getFileExtension(language));
    }
  }

  private String getFileExtension(String language) {
    // Mapeo de lenguajes a extensiones de archivo
    return switch (language.toLowerCase()) {
      case "java" -> ".java";
      case "python" -> ".py";
      case "javascript" -> ".js";
      case "typescript" -> ".ts";
      case "go" -> ".go";
      case "rust" -> ".rs";
      default -> ".txt";
    };
  }
}