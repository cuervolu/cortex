package com.cortex.backend.education.roadmap.api;

import com.cortex.backend.core.domain.Roadmap;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadmapRepository extends CrudRepository<Roadmap, Long> {

  Optional<Roadmap> findBySlug(String slug);
  
  Boolean existsBySlug(String slug);
}
