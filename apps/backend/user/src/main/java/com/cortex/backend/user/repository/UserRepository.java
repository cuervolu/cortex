package com.cortex.backend.user.repository;

import com.cortex.backend.core.domain.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  /**
   * Finds all users who are not mentors (i.e., potential mentees).
   *
   * @param pageable the pagination information
   * @return a Page of Users who are not mentors
   */
  @Query("""
        SELECT u
        FROM User u
        WHERE NOT EXISTS (
            SELECT r
            FROM u.roles r
            WHERE r.name = 'MENTOR'
        )
    """)
  Page<User> findAvailableMentees(Pageable pageable);
}
