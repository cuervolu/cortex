package com.cortex.backend.payments.internal;

import com.cortex.backend.core.domain.SubscriptionPlan;
import com.cortex.backend.payments.api.dto.CreateSubscriptionPlanDTO;
import com.cortex.backend.payments.api.dto.SubscriptionPlanDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionPlanMapper {

  SubscriptionPlanDTO toDTO(SubscriptionPlan subscriptionPlan);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "mercadoPagoPlanId", ignore = true)
  SubscriptionPlan toEntity(SubscriptionPlanDTO subscriptionPlanDTO);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "mercadoPagoPlanId", ignore = true)
  SubscriptionPlan toEntity(CreateSubscriptionPlanDTO createSubscriptionPlanDTO);

  List<SubscriptionPlanDTO> toDTOList(List<SubscriptionPlan> subscriptionPlans);
}