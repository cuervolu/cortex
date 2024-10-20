package com.cortex.backend.education.achievement.api;

import com.cortex.backend.core.domain.Achievement;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementRepository extends CrudRepository<Achievement, Long> {
  Optional<Achievement> findByName(String name);
}