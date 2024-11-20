package com.cortex.backend.lemonsqueezy.licensekeys.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LicenseMetadata(
    @JsonProperty("store_id")
    Long storeId,
    @JsonProperty("order_id")
    Long orderId,
    @JsonProperty("order_item_id")
    Long orderItemId,
    @JsonProperty("product_id")
    Long productId,
    @JsonProperty("product_name")
    String productName,
    @JsonProperty("variant_id")
    Long variantId,
    @JsonProperty("variant_name")
    String variantName,
    @JsonProperty("customer_id")
    Long customerId,
    @JsonProperty("customer_name")
    String customerName,
    @JsonProperty("customer_email")
    String customerEmail
) {

}
