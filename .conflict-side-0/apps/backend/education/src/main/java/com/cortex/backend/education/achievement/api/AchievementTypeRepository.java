package com.cortex.backend.education.achievement.api;

import com.cortex.backend.core.domain.AchievementType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementTypeRepository extends CrudRepository<AchievementType, Long> {
}
