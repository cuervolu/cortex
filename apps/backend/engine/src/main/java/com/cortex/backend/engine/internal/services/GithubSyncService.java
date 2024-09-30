package com.cortex.backend.engine.internal.services;

import com.cortex.backend.core.common.exception.GitSyncException;
import com.cortex.backend.engine.api.ExerciseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
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


  private String lastSyncedCommit;

  @EventListener(ApplicationReadyEvent.class)
  public void initializeExercises() {
    if (!exerciseService.areLessonsAvailable()) {
      log.warn("No lessons found in the database. Skipping exercise initialization.");
      return;
    }
    Path localPath = Path.of(localPathString);
    if (!Files.exists(localPath)) {
      log.info("Local repository does not exist. Cloning from GitHub...");
      cloneRepository(localPath);
    }
    syncExercises();
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
    log.info("Checking for updates in repository at {}", localPath);
    try {
      if (pullLatestChanges(localPath)) {
        updateExercisesFromLocalRepo(localPath);
      } else {
        log.info("No new changes in the repository. Skipping update.");
      }
    } catch (Exception e) {
      log.error("Failed to sync exercises", e);
      throw new GitSyncException("Failed to sync exercises", e);
    }
  }

  private boolean pullLatestChanges(Path localPath) throws Exception {
    try (Repository repository = new FileRepositoryBuilder()
        .setGitDir(new File(localPath.toFile(), ".git"))
        .build();
        Git git = new Git(repository)) {

      ObjectId oldHead = repository.resolve("HEAD");

      git.pull().setRemoteBranchName(branch).call();

      ObjectId newHead = repository.resolve("HEAD");

      if (!newHead.equals(oldHead)) {
        RevCommit latestCommit = git.log().setMaxCount(1).call().iterator().next();
        String newCommitId = latestCommit.getName();
        log.info("New changes detected. Latest commit: {}", newCommitId);
        lastSyncedCommit = newCommitId;
        return true;
      }

      return false;
    } catch (Exception e) {
      log.error("Error pulling latest changes", e);
      throw e;
    }
  }

  private void cloneRepository(Path localPath) {
    try (Git git = Git.cloneRepository()
        .setURI(repoUrl)
        .setDirectory(localPath.toFile())
        .setBranch(branch)
        .call()) {
      log.info("Successfully cloned repository to {}", localPath);
      RevCommit latestCommit = git.log().setMaxCount(1).call().iterator().next();
      lastSyncedCommit = latestCommit.getName();
      log.info("Latest commit after cloning: {}", lastSyncedCommit);
    } catch (Exception e) {
      log.error("Failed to clone repository", e);
      throw new GitSyncException("Failed to clone repository", e);
    }
  }

  private void updateExercisesFromLocalRepo(Path localPath) {
    log.info("Updating exercises from local repository");
    File exercisesDir = localPath.resolve("exercises").toFile();
    log.info("Exercises directory: {}", exercisesDir);

    if (isInvalidDirectory(exercisesDir)) {
      log.warn("Exercises directory does not exist or is not a directory: {}", exercisesDir);
      return;
    }

    processLanguageDirectories(exercisesDir);
  }
  
  private boolean isInvalidDirectory(File directory) {
    return !directory.exists() || !directory.isDirectory();
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

    if (isInvalidDirectory(practiceDir)) {
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