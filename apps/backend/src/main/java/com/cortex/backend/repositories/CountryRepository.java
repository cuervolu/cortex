package com.cortex.backend.repositories;

import com.cortex.backend.entities.user.Country;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends CrudRepository<Country, String> {

}
