package com.cortex.backend.engine.internal.services;

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
import com.cortex.backend.engine.internal.CodeFileReader;
import com.cortex.backend.engine.internal.mappers.ExerciseMapper;
import com.cortex.backend.engine.internal.utils.HashUtil;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
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
        createNewExercise(exerciseName, githubPath, instructions, hints);
      } else {
        updateExistingExercise(existingExercise, exerciseName, instructions, hints);
      }
    } catch (Exception e) {
      log.error("Error updating or creating exercise: {}", exerciseName, e);
      throw new ExerciseCreationException("Failed to update or create exercise", e);
    }
  }

  private void createNewExercise(String exerciseName, String githubPath, String instructions,
      String hints) {
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

    String slug = slugUtils.generateUniqueSlug(exerciseName,
        s -> exerciseRepository.findBySlug(s).isPresent());
    newExercise.setSlug(slug);

    exerciseRepository.save(newExercise);
    log.info("New exercise created with ID: {} and slug: {}", newExercise.getId(),
        newExercise.getSlug());
  }

  private void updateExistingExercise(Exercise existingExercise, String exerciseName,
      String instructions, String hints) {
    log.info("Updating existing exercise: {}", exerciseName);
    existingExercise.setLastGithubSync(LocalDateTime.now());
    existingExercise.setInstructions(instructions);
    existingExercise.setHints(hints);

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


  @Override
  @Transactional(readOnly = true)
  public ExerciseDetailsResponse getExerciseDetails(Long id) {
    Exercise exercise = exerciseRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Exercise not found with id: " + id));

    Path exercisePath = Paths.get(localRepoPath, exercise.getGithubPath());

    try {
      String initialCode = codeFileReader.readInitialCode(exercisePath);
      String testCode = codeFileReader.readTestCode(exercisePath);

      String contentHash = generateContentHash(initialCode, testCode);

      return ExerciseDetailsResponse.builder()
          .id(exercise.getId())
          .title(exercise.getTitle())
          .instructions(exercise.getInstructions())
          .hints(exercise.getHints())
          .initialCode(initialCode)
          .testCode(testCode)
          .contentHash(contentHash)
          .build();
    } catch (IOException e) {
      log.error("Error reading exercise files for exercise id: {}", id, e);
      throw new ExerciseReadException("Failed to read exercise files", e);
    } 
  }
  
  private String generateContentHash(String initialCode, String testCode) {
    String content = initialCode + testCode;
    return HashUtil.generateSHA256Hash(content);
  }
  
  @Override
  public boolean areLessonsAvailable() {
    return lessonRepository.count() > 0;
  }

}