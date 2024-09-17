package com.cortex.backend.infrastructure.persistence;

import com.cortex.backend.media.domain.Media;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends CrudRepository<Media, Long> {

}
