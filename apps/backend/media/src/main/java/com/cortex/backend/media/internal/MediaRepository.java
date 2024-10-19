package com.cortex.backend.media.internal;

import com.cortex.backend.core.domain.Media;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends CrudRepository<Media, Long> {

}
