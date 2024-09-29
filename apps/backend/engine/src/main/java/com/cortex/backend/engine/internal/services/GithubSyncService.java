package com.cortex.backend.engine.internal.services;

import com.cortex.backend.core.common.exception.GitSyncException;
import com.cortex.backend.engine.api.ExerciseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class GithubSyncService {

  private final ExerciseService exerciseService;

  @Value("${github.exercises.repo-url}")
  private String repoUrl;

  @Value("${github.exercises.local-path}")
  private String localPathString;

  @Value("${github.exercises.branch}")
  private String branch;

  @EventListener(ApplicationReadyEvent.class)
  public void initializeExercises() {
    if (!exerciseService.areLessonsAvailable()) {
      log.warn("No lessons found in the database. Skipping exercise initialization.");
      return;
    }
    if (exerciseService.isExerciseRepositoryEmpty()) {
      log.info("No exercises found. Initializing from GitHub...");
      syncExercises();
    }
  }

  @Scheduled(fixedRateString = "${github.exercises.sync-interval-ms}")
  public void scheduledSync() {
    if (!exerciseService.areLessonsAvailable()) {
      log.warn("No lessons found in the database. Skipping scheduled sync.");
      return;
    }
    log.info("Starting scheduled sync of exercises");
    syncExercises();
  }

  public void syncExercises() {
    if (!exerciseService.areLessonsAvailable()) {
      log.warn("No lessons found in the database. Skipping exercise sync.");
      return;
    }
    Path localPath = Path.of(localPathString);
    log.info("Syncing exercises from {} to {}", repoUrl, localPath);
    try {
      if (Files.exists(localPath)) {
        pullLatestChanges(localPath);
      } else {
        cloneRepository(localPath);
      }
      updateExercisesFromLocalRepo(localPath);
    } catch (Exception e) {
      log.error("Failed to sync exercises", e);
      throw new GitSyncException("Failed to sync exercises", e);
    }
  }

  private void pullLatestChanges(Path localPath) throws Exception {
    try (Git git = Git.open(localPath.toFile())) {
      git.pull().setRemoteBranchName(branch).call();
      log.info("Successfully pulled latest changes from remote repository");
    } catch (Exception e) {
      log.error("Error pulling latest changes", e);
      throw e;
    }
  }

  private void cloneRepository(Path localPath) throws Exception {
    try (Git _ = Git.cloneRepository()
        .setURI(repoUrl)
        .setDirectory(localPath.toFile())
        .setBranch(branch)
        .call()) {
      log.info("Successfully cloned repository to {}", localPath);
    }
  }

  private void updateExercisesFromLocalRepo(Path localPath) {
    log.info("Updating exercises from local repository");
    File exercisesDir = localPath.resolve("exercises").toFile();
    log.info("Exercises directory: {}", exercisesDir);

    if (!isValidDirectory(exercisesDir)) {
      log.warn("Exercises directory does not exist or is not a directory: {}", exercisesDir);
      return;
    }

    processLanguageDirectories(exercisesDir);
  }

  private boolean isValidDirectory(File directory) {
    return directory.exists() && directory.isDirectory();
  }

  private void processLanguageDirectories(File exercisesDir) {
    for (File languageDir : Objects.requireNonNull(exercisesDir.listFiles(File::isDirectory))) {
      if (isHiddenOrSystemDirectory(languageDir)) {
        continue;
      }
      processPracticeDirectory(languageDir);
    }
  }

  private void processPracticeDirectory(File languageDir) {
    log.info("Language directory: {}", languageDir);
    File practiceDir = new File(languageDir, "practice");
    log.info("Practice directory: {}", practiceDir);

    if (!isValidDirectory(practiceDir)) {
      log.warn("Practice directory does not exist or is not a directory: {}", practiceDir);
      return;
    }

    processExerciseDirectories(practiceDir, languageDir.getName());
  }

  private void processExerciseDirectories(File practiceDir, String languageName) {
    for (File exerciseDir : Objects.requireNonNull(practiceDir.listFiles(File::isDirectory))) {
      if (isHiddenOrSystemDirectory(exerciseDir)) {
        continue;
      }
      log.info("Updating exercise: {}", exerciseDir);
      updateExercise(exerciseDir, languageName);
    }
  }


  private boolean isHiddenOrSystemDirectory(File directory) {
    return directory.isHidden() || directory.getName().startsWith(".");
  }

  private void updateExercise(File exerciseDir, String language) {
    String exerciseName = exerciseDir.getName();
    String githubPath = "exercises" + File.separatorChar + language + File.separatorChar + "practice" + File.separatorChar + exerciseName;
    String instructions = readFileContent(exerciseDir, ".docs/instructions.md");
    String hints = readFileContent(exerciseDir, ".docs/hints.md");

    log.info("Updating exercise: {} ({})", exerciseName, githubPath);
    log.debug("Instructions length: {}, Hints length: {}", instructions.length(), hints.length());

    exerciseService.updateOrCreateExercise(exerciseName, githubPath, instructions, hints);
  }

  private String readFileContent(File exerciseDir, String relativePath) {
    try {
      Path filePath = exerciseDir.toPath().resolve(relativePath);
      return Files.exists(filePath) ? Files.readString(filePath) : "";
    } catch (Exception e) {
      log.error("Error reading file: {}", relativePath, e);
      return "";
    }
  }
}