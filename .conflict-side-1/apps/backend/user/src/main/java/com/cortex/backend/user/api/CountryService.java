package com.cortex.backend.user.api;

import com.cortex.backend.core.domain.Country;
import com.cortex.backend.user.repository.CountryRepository;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
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
    log.info("Countries are already initialized");
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