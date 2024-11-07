package com.cortex.backend.payments.internal;

import com.cortex.backend.core.domain.Plan;
import com.cortex.backend.lemonsqueezy.products.Product;
import com.cortex.backend.lemonsqueezy.products.ProductFilter;
import com.cortex.backend.lemonsqueezy.products.ProductService;
import com.cortex.backend.lemonsqueezy.variants.Variant;
import com.cortex.backend.lemonsqueezy.variants.VariantService;
import com.cortex.backend.lemonsqueezy.variants.VariantFilter;
import com.cortex.backend.lemonsqueezy.prices.Price;
import com.cortex.backend.lemonsqueezy.prices.PriceService;
import com.cortex.backend.lemonsqueezy.prices.PriceFilter;
import com.cortex.backend.payments.api.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanSyncService {

  private final ProductService productService;
  private final VariantService variantService;
  private final PriceService priceService;
  private final PlanRepository planRepository;

  @Value("${lemon-squeezy.store-id}")
  private Long storeId;

  @Transactional
  public List<Plan> syncPlans() throws IOException {
    log.info("Starting plan synchronization with Lemon Squeezy");
    List<Plan> syncedPlans = new ArrayList<>();
    var productsResponse = productService.listProducts(
        ProductFilter.builder().storeId(storeId).build(),
        null
    );

    for (Product product : productsResponse.getData()) {
      var variantsResponse = variantService.listVariants(
          VariantFilter.builder()
              .productId(Long.valueOf(product.getId()))
              .build(),
          null
      );

      for (Variant variant : variantsResponse.getData()) {
        if (variant.getStatus().equals("draft") ||
            (variantsResponse.getData().size() > 1 && variant.getStatus().equals("pending"))) {
          log.debug("Skipping variant {} - status: {}", variant.getId(), variant.getStatus());
          continue;
        }
        
        var pricesResponse = priceService.listPrices(
            PriceFilter.builder()
                .variantId(Long.valueOf(variant.getId()))
                .build(),
            null
        );

        if (pricesResponse.getData().isEmpty()) {
          log.debug("Skipping variant {} as it has no prices", variant.getId());
          continue;
        }
        
        Price currentPrice = pricesResponse.getData().getFirst();
        
        if (!"subscription".equals(currentPrice.getCategory())) {
          log.debug("Skipping variant {} as it's not a subscription", variant.getId());
          continue;
        }

        try {
          Plan plan = buildPlan(variant, product, currentPrice);
          plan = savePlan(plan);
          syncedPlans.add(plan);

          log.info("Synced plan: {} (Variant ID: {})", plan.getName(), plan.getVariantId());
        } catch (Exception e) {
          log.error("Error syncing plan for variant {}: {}", variant.getId(), e.getMessage());
        }
      }
    }

    log.info("Plan synchronization completed. Synced {} plans", syncedPlans.size());
    return syncedPlans;
  }

  private Plan buildPlan(Variant variant, Product product, Price price) {
    boolean isUsageBased = price.getUsageAggregation() != null;
    String priceValue;

    if (isUsageBased) {
      priceValue = price.getUnitPriceDecimal();
    } else if ("graduated".equals(price.getScheme()) || "volume".equals(price.getScheme())) {
      var firstTier = price.getTiers().getFirst();
      priceValue = String.valueOf(firstTier.getUnitPrice());
    } else {
      priceValue = String.valueOf(price.getUnitPrice());
    }

    return Plan.builder()
        .variantId(Long.parseLong(variant.getId()))
        .productId(Long.parseLong(product.getId()))
        .productName(product.getName())
        .name(variant.getName())
        .description(variant.getDescription())
        .price(priceValue)
        .isUsageBased(isUsageBased)
        .interval(price.getRenewalIntervalUnit())
        .intervalCount(price.getRenewalIntervalQuantity())
        .trialInterval(price.getTrialIntervalUnit())
        .trialIntervalCount(price.getTrialIntervalQuantity())
        .sort(variant.getSort())
        .build();
  }

  private Plan savePlan(Plan plan) {
    return planRepository.findByVariantId(plan.getVariantId())
        .map(existingPlan -> {
          existingPlan.setProductId(plan.getProductId());
          existingPlan.setProductName(plan.getProductName());
          existingPlan.setName(plan.getName());
          existingPlan.setDescription(plan.getDescription());
          existingPlan.setPrice(plan.getPrice());
          existingPlan.setUsageBased(plan.isUsageBased());
          existingPlan.setInterval(plan.getInterval());
          existingPlan.setIntervalCount(plan.getIntervalCount());
          existingPlan.setTrialInterval(plan.getTrialInterval());
          existingPlan.setTrialIntervalCount(plan.getTrialIntervalCount());
          existingPlan.setSort(plan.getSort());
          return planRepository.save(existingPlan);
        })
        .orElseGet(() -> planRepository.save(plan));
  }
}