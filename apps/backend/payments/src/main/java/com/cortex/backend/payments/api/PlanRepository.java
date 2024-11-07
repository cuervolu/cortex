package com.cortex.backend.payments.api;

import com.cortex.backend.core.domain.Plan;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface PlanRepository extends CrudRepository<Plan, Long> {
  Optional<Plan> findByVariantId(Long variantId);
}