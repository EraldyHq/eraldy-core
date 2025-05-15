package com.eraldy.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Docker CLI wrapper using Docker Java API Client
 */
public class DockerCli {

  private final DockerClient dockerClient;
  private final DockerContainer dockerContainer;

  /**
   * Constructor with DockerContainer
   *
   * @param dockerContainer the container configuration
   */
  public DockerCli(DockerContainer dockerContainer) {
    this.dockerContainer = dockerContainer;

    // Configure and build Docker client for version 3.5.0
    DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
    DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .sslConfig(config.getSSLConfig())
            .build();
    this.dockerClient = DockerClientImpl.getInstance(config, httpClient);
  }

  /**
   * Check if a container exists
   *
   * @return true if the container exists, false otherwise
   */
  public boolean exists() {
    String containerName = dockerContainer.getName();
    try {
      dockerClient.inspectContainerCmd(containerName).exec();
      return true;
    } catch (NotFoundException e) {
      return false;
    }
  }

  /**
   * Run the container
   * - If container exists, start it
   * - If container doesn't exist, create and run it
   */
  public void run() {
    String containerName = dockerContainer.getName();

    if (exists()) {
      // Container exists, start it if not running
      try {
        dockerClient.startContainerCmd(containerName).exec();
        System.err.println("Container " + containerName + " started");
      } catch (Exception e) {
        System.err.println("Failed to start container: " + e.getMessage());
      }
    } else {
      // Container doesn't exist, create and run it
      createAndRunContainer();
    }
  }

  /**
   * Stop the container if it exists
   */
  public void stop() {
    String containerName = dockerContainer.getName();

    if (exists()) {
      // Container exists, stop it
      try {
        dockerClient.stopContainerCmd(containerName).exec();
        System.out.println("Container " + containerName + " stopped");
      } catch (Exception e) {
        System.err.println("Failed to stop container: " + e.getMessage());
      }
    } else {
      System.out.println("Container " + containerName + " does not exist");
    }
  }

  /**
   * Remove the container if it exists
   */
  public void rm() {
    String containerName = dockerContainer.getName();

    if (exists()) {
      // Container exists, remove it
      try {
        dockerClient.removeContainerCmd(containerName).exec();
        System.out.println("Container " + containerName + " removed");
      } catch (Exception e) {
        System.err.println("Failed to remove container: " + e.getMessage());
      }
    } else {
      System.out.println("Container " + containerName + " does not exist");
    }
  }

  /**
   * Create and run a new container based on DockerContainer configuration
   */
  private void createAndRunContainer() {
    String containerName = dockerContainer.getName();
    String image = dockerContainer.getImage();
    Map<Integer, Integer> portMap = dockerContainer.getPortBinding();
    Map<Path, Path> volumeMap = dockerContainer.getVolumeBinding();

    try {
      // Prepare port bindings
      List<PortBinding> portBindings = new ArrayList<>();
      List<ExposedPort> exposedPorts = new ArrayList<>();

      for (Map.Entry<Integer, Integer> entry : portMap.entrySet()) {
        Integer hostPort = entry.getKey();
        Integer containerPort = entry.getValue();

        ExposedPort exposedPort = ExposedPort.tcp(containerPort);
        exposedPorts.add(exposedPort);
        portBindings.add(new PortBinding(Ports.Binding.bindPort(hostPort), exposedPort));
      }

      // Prepare volume bindings
      List<Bind> binds = new ArrayList<>();
      for (Map.Entry<Path, Path> entry : volumeMap.entrySet()) {
        Path hostPath = entry.getKey();
        Path containerPath = entry.getValue();

        Volume volume = new Volume(containerPath.toString());
        binds.add(new Bind(hostPath.toString(), volume));
      }

      // Create host config with port and volume bindings
      HostConfig hostConfig = new HostConfig()
        .withPortBindings(portBindings)
        .withBinds(binds);

      // Create container
      CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(image)
        .withName(containerName)
        .withHostConfig(hostConfig)
        .withExposedPorts(exposedPorts);

      CreateContainerResponse container = createContainerCmd.exec();

      // Start container
      dockerClient.startContainerCmd(container.getId()).exec();
      System.out.println("Container " + containerName + " created and started");

    } catch (Exception e) {
      System.err.println("Failed to create and run container: " + e.getMessage());
    }
  }
}
