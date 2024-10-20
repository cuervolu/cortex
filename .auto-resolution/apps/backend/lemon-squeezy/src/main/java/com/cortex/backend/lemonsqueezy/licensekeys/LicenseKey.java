package com.cortex.backend.lemonsqueezy.licensekeys;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

/**
 * Represents a Lemon Squeezy license key object.
 */
@Data
public class LicenseKey {

  /**
   * Unique identifier for the license key
   */
  private String id;

  /**
   * The ID of the store this license key belongs to.
   */
  @JsonProperty("store_id")
  private long storeId;

  /**
   * The ID of the customer this license key belongs to.
   */
  @JsonProperty("customer_id")
  private long customerId;

  /**
   * The ID of the order associated with this license key.
   */
  @JsonProperty("order_id")
  private long orderId;

  /**
   * The ID of the order item associated with this license key.
   */
  @JsonProperty("order_item_id")
  private long orderItemId;

  /**
   * The ID of the product associated with this license key.
   */
  @JsonProperty("product_id")
  private long productId;

  /**
   * The full name of the customer.
   */
  @JsonProperty("user_name")
  private String userName;

  /**
   * The email address of the customer.
   */
  @JsonProperty("user_email")
  private String userEmail;

  /**
   * The full license key.
   */
  private String key;

  /**
   * A "short" representation of the license key, made up of the string "XXXX-" followed by the last
   * 12 characters of the license key.
   */
  @JsonProperty("key_short")
  private String keyShort;

  /**
   * The activation limit of this license key.
   */
  @JsonProperty("activation_limit")
  private int activationLimit;

  /**
   * A count of the number of instances this license key has been activated on.
   */
  @JsonProperty("instances_count")
  private int instancesCount;

  /**
   * Has the value `true` if this license key has been disabled.
   */
  private boolean disabled;

  /**
   * The status of the license key. One of:
   * - inactive
   * - active
   * - expired
   * - disabled
   */
  private String status;

  /**
   * The formatted status of the license key.
   */
  @JsonProperty("status_formatted")
  private String statusFormatted;

  /**
   * An ISO 8601 formatted date-time string indicating when the license key expires. Can be `null`
   * if the license key is perpetual.
   */
  @JsonProperty("expires_at")
  private ZonedDateTime expiresAt;

  /**
   * An ISO 8601 formatted date-time string indicating when the object was created.
   */
  @JsonProperty("created_at")
  private ZonedDateTime createdAt;

  /**
   * An ISO 8601 formatted date-time string indicating when the object was last updated.
   */
  @JsonProperty("updated_at")
  private ZonedDateTime updatedAt;
}