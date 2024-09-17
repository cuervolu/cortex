package com.cortex.backend.repositories;

import com.cortex.backend.entities.Media;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends CrudRepository<Media, Long> {

}
