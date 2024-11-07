package com.cortex.backend.education.roadmap.api;

import com.cortex.backend.core.domain.Roadmap;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoadmapRepository extends CrudRepository<Roadmap, Long> {

  Optional<Roadmap> findBySlug(String slug);
  
  Boolean existsBySlug(String slug);

  @Query("""
      SELECT  rodmap
      FROM Roadmap rodmap
      WHERE rodmap.isPublished = true
      """)
  Page<Roadmap> findAllPublishedRoadmaps(Pageable pageable);

  @Query("""
    SELECT DISTINCT r FROM Roadmap r
    LEFT JOIN FETCH r.courses c
    LEFT JOIN FETCH c.moduleEntities m
    LEFT JOIN FETCH m.lessons l
    LEFT JOIN FETCH l.exercises e
    WHERE r.slug = :slug AND r.isPublished = true
    """)
  Optional<Roadmap> findBySlugWithDetails(@Param("slug") String slug);
}
