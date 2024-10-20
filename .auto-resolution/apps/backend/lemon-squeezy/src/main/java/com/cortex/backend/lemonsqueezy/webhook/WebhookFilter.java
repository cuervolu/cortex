package com.cortex.backend.lemonsqueezy.webhook;

import lombok.Builder;

/**
 * Filter for listing webhooks.
 *
 * @param storeId Only return webhooks belonging to the store with this ID.
 */
@Builder
public record WebhookFilter(Long storeId) {

}
