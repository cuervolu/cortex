package com.cortex.backend.education.progress.api;

import com.cortex.backend.core.domain.EntityType;
import com.cortex.backend.core.domain.ProgressStatus;
import com.cortex.backend.core.domain.UserProgress;
import com.cortex.backend.core.domain.UserProgressKey;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProgressRepository extends CrudRepository<UserProgress, UserProgressKey> {

  List<UserProgress> findByIdUserId(Long userId);

  List<UserProgress> findByIdUserIdAndIdEntityType(Long userId, EntityType entityType);

  @Query("SELECT SUM(e.points) FROM UserProgress up " +
      "JOIN Exercise e ON up.id.entityId = e.id " +
      "WHERE up.id.userId = :userId AND up.id.entityType = 'EXERCISE' AND up.status = 'COMPLETED'")
  Integer sumPointsByUserId(Long userId);

  @Query("SELECT SUM(l.credits) FROM UserProgress up " +
      "JOIN Lesson l ON up.id.entityId = l.id " +
      "WHERE up.id.userId = :userId AND up.id.entityType = 'LESSON' AND up.status = 'COMPLETED'")
  Integer sumCreditsByUserId(Long userId);

  @Query("SELECT DISTINCT CAST(up.completionDate AS LocalDate) FROM UserProgress up " +
      "WHERE up.id.userId = :userId ORDER BY up.completionDate DESC")
  List<LocalDate> findDistinctActiveDatesByUserId(Long userId);

  Long countByIdUserIdAndIdEntityTypeAndStatus(Long userId, EntityType entityType,
      ProgressStatus status);
}