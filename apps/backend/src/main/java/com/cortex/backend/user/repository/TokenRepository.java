package com.cortex.backend.user.repository;

import com.cortex.backend.user.domain.Token;
import com.cortex.backend.common.types.TokenType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<Token, Long> {
  Optional<Token> findByToken(String token);
  
  Optional<Token> findByTokenAndTokenType(String token, TokenType tokenType);

  @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND t.expiresAt > CURRENT_TIMESTAMP")
  List<Token> findAllValidTokenByUser(Long userId);
}
