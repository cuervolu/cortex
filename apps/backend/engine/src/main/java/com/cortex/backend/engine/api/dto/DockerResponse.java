package com.cortex.backend.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DockerResponse {

  private String version;

  @JsonProperty("api_version")
  private String apiVersion;

  @JsonProperty("operating_system")
  private String operatingSystem;
  private String architecture;

  @JsonProperty("kernel_version")
  private String kernelVersion;

  @JsonProperty("go_version")
  private String goVersion;

  @JsonProperty("experimental_build")
  private String experimentalBuild;

  @JsonProperty("server_version")
  private String serverVersion;
}
