package com.cortex.backend.engine.api;

import com.cortex.backend.engine.api.dto.DockerResponse;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.Version;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/docker")
@RequiredArgsConstructor
public class DockerController {

  private final DockerClient dockerClient;

  @GetMapping("/info")
  public ResponseEntity<DockerResponse> getDockerInfo() {
    Version version = dockerClient.versionCmd().exec();
    Info info = dockerClient.infoCmd().exec();

    DockerResponse dockerInfo =
        DockerResponse.builder()
            .version(version.getVersion())
            .apiVersion(version.getApiVersion())
            .operatingSystem(version.getOperatingSystem())
            .architecture(version.getArch())
            .kernelVersion(version.getKernelVersion())
            .goVersion(version.getGoVersion())
            .experimentalBuild(Boolean.toString(Boolean.TRUE.equals(version.getExperimental())))
            .serverVersion(info.getServerVersion())
            .build();

    return ResponseEntity.ok(dockerInfo);
  }

}
