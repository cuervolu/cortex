package com.cortex.backend.education.progress.api;

import com.cortex.backend.core.domain.UserAchievement;
import com.cortex.backend.core.domain.UserAchievementKey;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAchievementRepository extends CrudRepository<UserAchievement, UserAchievementKey> {
  List<UserAchievement> findByIdUserId(Long userId);
}