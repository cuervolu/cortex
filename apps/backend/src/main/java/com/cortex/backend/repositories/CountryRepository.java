package com.cortex.backend.repositories;

import com.cortex.backend.entities.user.Country;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends CrudRepository<Country, String> {

  Optional<Country> findByCode(String code);
}
