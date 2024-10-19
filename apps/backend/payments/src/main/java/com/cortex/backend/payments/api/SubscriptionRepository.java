package com.cortex.backend.payments.api;

import com.cortex.backend.core.domain.Subscription;
import com.cortex.backend.core.domain.SubscriptionStatus;
import com.cortex.backend.core.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Long> {
    List<Subscription> findByUserAndStatus(User user, SubscriptionStatus status);

  Optional<Subscription> findByUserAndStatusAndPlanId(User user, SubscriptionStatus status,
      Long planId);

  List<Subscription> findByStatus(SubscriptionStatus status);
}