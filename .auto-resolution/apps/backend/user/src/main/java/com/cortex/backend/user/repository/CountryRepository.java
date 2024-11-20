package com.cortex.backend.user.repository;

import com.cortex.backend.core.domain.Country;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends CrudRepository<Country, String> {

  Optional<Country> findByCode(String code);
}
