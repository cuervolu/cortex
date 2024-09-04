package com.cortex.backend.repositories;

import com.cortex.backend.entities.user.Token;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<Token, Long> {
  Optional<Token> findByToken(String token);
}
