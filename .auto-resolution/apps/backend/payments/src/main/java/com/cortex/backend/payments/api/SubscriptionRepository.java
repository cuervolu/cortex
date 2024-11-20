package com.cortex.backend.payments.api;

import com.cortex.backend.core.domain.Subscription;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {

  Optional<Subscription> findByLemonSqueezyId(String lemonSqueezyId);

  List<Subscription> findByUserId(String userId);
}