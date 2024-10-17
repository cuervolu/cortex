package com.cortex.backend.payments.api;

import com.cortex.backend.core.domain.Refund;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository extends CrudRepository<Refund, Long> {
  List<Refund> findByTransactionId(Long transactionId);
}