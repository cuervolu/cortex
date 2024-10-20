package com.cortex.backend.payments.api;

import com.cortex.backend.core.domain.SubscriptionPlan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPlanRepository extends CrudRepository<SubscriptionPlan, Long> {

}
