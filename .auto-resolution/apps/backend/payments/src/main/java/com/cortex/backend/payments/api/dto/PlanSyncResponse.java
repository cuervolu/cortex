package com.cortex.backend.payments.api.dto;

import com.cortex.backend.core.domain.Plan;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response object for plan synchronization")
public record PlanSyncResponse(
    @Schema(description = "Indicates if the synchronization was successful")
    boolean success,

    @Schema(description = "Description of the synchronization result or error message if failed")
    String message,

    @Schema(description = "Number of plans successfully synchronized")
    @JsonProperty("synced_plans_count")
    Integer syncedPlansCount,

    @Schema(description = "Time taken to execute the synchronization in seconds")
    @JsonProperty("execution_time_seconds")
    String executionTimeSeconds,

    @Schema(description = "List of synchronized plans")
    List<Plan> plans
) {
  public static PlanSyncResponseBuilder builder() {
    return new PlanSyncResponseBuilder();
  }

  /**
   * Builder pattern implementation for PlanSyncResponse
   * Mantains builder compatibility for easy migration
   */
  public static class PlanSyncResponseBuilder {
    private boolean success;
    private String message;
    private Integer syncedPlansCount;
    private String executionTimeSeconds;
    private List<Plan> plans;

    PlanSyncResponseBuilder() {
    }

    public PlanSyncResponseBuilder success(boolean success) {
      this.success = success;
      return this;
    }

    public PlanSyncResponseBuilder message(String message) {
      this.message = message;
      return this;
    }

    public PlanSyncResponseBuilder syncedPlansCount(Integer syncedPlansCount) {
      this.syncedPlansCount = syncedPlansCount;
      return this;
    }

    public PlanSyncResponseBuilder executionTimeSeconds(String executionTimeSeconds) {
      this.executionTimeSeconds = executionTimeSeconds;
      return this;
    }

    public PlanSyncResponseBuilder plans(List<Plan> plans) {
      this.plans = plans;
      return this;
    }

    public PlanSyncResponse build() {
      return new PlanSyncResponse(success, message, syncedPlansCount, executionTimeSeconds, plans);
    }
  }
}