package com.cortex.backend.engine.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import lombok.Getter;

public class AutoCloseableContainer implements AutoCloseable {
  @Getter
  private final CreateContainerResponse container;
  private final DockerClient dockerClient;

  public AutoCloseableContainer(CreateContainerResponse container, DockerClient dockerClient) {
    this.container = container;
    this.dockerClient = dockerClient;
  }

  @Override
  public void close() {
    dockerClient.removeContainerCmd(container.getId()).withForce(true).exec();
  }
}