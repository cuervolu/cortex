package com.cortex.backend.education.progress.internal;

import com.cortex.backend.core.domain.Lesson;
import com.cortex.backend.core.domain.UserProgress;
import com.cortex.backend.core.domain.UserProgressKey;
import com.cortex.backend.core.domain.EntityType;
import com.cortex.backend.core.domain.ProgressStatus;
import com.cortex.backend.education.lesson.api.LessonRepository;
import com.cortex.backend.education.progress.api.UserProgressRepository;
import com.cortex.backend.education.progress.api.UserProgressService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProgressServiceImpl implements UserProgressService {

  private final UserProgressRepository userProgressRepository;
  private final LessonRepository lessonRepository;

  @Override
  @Transactional
  public UserProgress saveProgress(Long userId, Long entityId, EntityType entityType) {
    UserProgressKey key = new UserProgressKey(userId, entityId, entityType);
    UserProgress progress = userProgressRepository.findById(key)
        .orElse(new UserProgress(key, ProgressStatus.IN_PROGRESS, null));

    progress.setStatus(ProgressStatus.COMPLETED);
    progress.setCompletionDate(LocalDateTime.now());

    return userProgressRepository.save(progress);
  }

  @Override
  public List<UserProgress> getUserProgress(Long userId) {
    return userProgressRepository.findByIdUserId(userId);
  }

  @Override
  public List<UserProgress> getUserProgressByEntityType(Long userId, EntityType entityType) {
    return userProgressRepository.findByIdUserIdAndIdEntityType(userId, entityType);
  }

  @Override
  public boolean isEntityCompleted(Long userId, Long entityId, EntityType entityType) {
    UserProgressKey key = new UserProgressKey(userId, entityId, entityType);
    return userProgressRepository.findById(key)
        .map(progress -> progress.getStatus() == ProgressStatus.COMPLETED)
        .orElse(false);
  }

  @Override
  public int getTotalPoints(Long userId) {
    return userProgressRepository.sumPointsByUserId(userId);
  }

  @Override
  public int getTotalCredits(Long userId) {
    return userProgressRepository.sumCreditsByUserId(userId);
  }

  @Override
  public int getConsecutiveDaysActive(Long userId) {
    List<LocalDate> activeDates = userProgressRepository.findDistinctActiveDatesByUserId(userId);
    if (activeDates.isEmpty()) {
      return 0;
    }

    int consecutiveDays = 1;
    LocalDate currentDate = LocalDate.now();
    for (LocalDate date : activeDates) {
      if (date.equals(currentDate) || date.equals(currentDate.minusDays(1))) {
        currentDate = date;
        consecutiveDays++;
      } else {
        break;
      }
    }
    return consecutiveDays - 1; // We subtract 1 because we start counting from 1
  }

  @Override
  public long countCompletedEntities(Long userId, EntityType entityType) {
    return userProgressRepository.countByIdUserIdAndIdEntityTypeAndStatus(userId, entityType, ProgressStatus.COMPLETED);
  }
}