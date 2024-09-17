package com.cortex.backend.services;

import com.cortex.backend.entities.user.Country;
import com.cortex.backend.repositories.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CountryService {

  private final CountryRepository countryRepository;

  @Transactional
  @CacheEvict(value = "countries", allEntries = true)
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

  @Cacheable("countries")
  public List<Country> getAllCountries() {
    return (List<Country>) countryRepository.findAll();
  }

  @Cacheable(value = "countries", key = "#code")
  public Country getCountryByCode(String code) {
    return countryRepository.findByCode(code).orElse(null);
  }

  @CacheEvict(value = "countries", allEntries = true)
  public void updateCountry(Country country) {
    countryRepository.save(country);
  }
}