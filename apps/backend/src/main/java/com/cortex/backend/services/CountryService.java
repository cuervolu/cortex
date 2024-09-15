package com.cortex.backend.services;

import com.cortex.backend.entities.user.Country;
import com.cortex.backend.repositories.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CountryService {

  private final CountryRepository countryRepository;

  @Transactional
  public void initializeCountries() {
    if (countryRepository.count() == 0) {
      List<Country> countries = Locale.availableLocales()
          .filter(locale -> !locale.getCountry().isEmpty())
          .map(locale -> {
            Country country = new Country();
            country.setCode(locale.getCountry());
            country.setName(locale.getDisplayCountry(Locale.ENGLISH));
            return country;
          })
          .distinct()
          .toList();

      countryRepository.saveAll(countries);
    }
  }

  public List<Country> getAllCountries() {
    return (List<Country>) countryRepository.findAll();
  }
}