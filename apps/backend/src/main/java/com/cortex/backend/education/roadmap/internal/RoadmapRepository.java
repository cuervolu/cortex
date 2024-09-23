package com.cortex.backend.education.roadmap.internal;

import com.cortex.backend.education.roadmap.domain.Roadmap;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadmapRepository extends CrudRepository<Roadmap, Long> {

  Optional<Roadmap> findBySlug(String slug);
  
  Boolean existsBySlug(String slug);
}
