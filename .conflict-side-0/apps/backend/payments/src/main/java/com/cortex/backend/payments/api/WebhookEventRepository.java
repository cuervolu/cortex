package com.cortex.backend.payments.api;

import com.cortex.backend.core.domain.WebhookEvent;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface WebhookEventRepository extends CrudRepository<WebhookEvent, Long> {
  List<WebhookEvent> findByProcessedOrderByCreatedAtAsc(boolean processed);
}