package com.cortex.backend.engine.internal;

import com.cortex.backend.core.common.SlugUtils;
import com.cortex.backend.core.common.exception.ExerciseCreationException;
import com.cortex.backend.core.domain.Exercise;
import com.cortex.backend.education.lesson.internal.LessonRepository;
import com.cortex.backend.engine.api.ExerciseRepository;
import com.cortex.backend.engine.api.ExerciseService;
import com.cortex.backend.engine.api.dto.CreateExercise;
import com.cortex.backend.engine.api.dto.ExerciseResponse;
import com.cortex.backend.engine.api.dto.UpdateExercise;
import jakarta.persistence.EntityNotFoundException;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  public void updateOrCreateExercise(String exerciseName, String githubPath, String instructions, String hints) {
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
        log.info("New exercise created with ID: {} and slug: {}", newExercise.getId(), newExercise.getSlug());
      } else {
        log.info("Updating existing exercise: {}", exerciseName);
        existingExercise.setLastGithubSync(LocalDateTime.now());
        existingExercise.setInstructions(instructions);
        existingExercise.setHints(hints);

        // Check if slug needs to be updated
        if (existingExercise.getSlug() == null || existingExercise.getSlug().isEmpty()) {
          String slug = slugUtils.generateUniqueSlug(exerciseName,
              s -> !s.equals(existingExercise.getSlug()) && exerciseRepository.findBySlug(s).isPresent());
          existingExercise.setSlug(slug);
        }

        exerciseRepository.save(existingExercise);
        log.info("Exercise updated with ID: {} and slug: {}", existingExercise.getId(), existingExercise.getSlug());
      }
    } catch (Exception e) {
      log.error("Error updating or creating exercise: {}", exerciseName, e);
      throw new ExerciseCreationException("Failed to update or create exercise", e);
    }
  }

  @Override
  public boolean areLessonsAvailable() {
    return lessonRepository.count() > 0;
  }

}