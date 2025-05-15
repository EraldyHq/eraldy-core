package com.eraldy.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import net.bytle.os.Oss;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DockerContainerTest {

  private DockerContainer dockerContainer;
  private DockerClient dockerClient;
  private static final String CONTAINER_NAME = "httpbin-test-container";
  private static final String IMAGE_NAME = "kennethreitz/httpbin";

  @BeforeEach
  void setUp() {
    // Create DockerContainer with httpbin image
    DockerContainer.Conf conf = DockerContainer.createConf(IMAGE_NAME);
    conf.setContainerName(CONTAINER_NAME);
    Integer port = Oss.getRandomAvailablePort();
    conf.setPortBinding(port, 80);

    dockerContainer = conf.build();
    if(dockerContainer.exists()){
      dockerContainer.rm();
    }

    // Create Docker client for verification
    DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
    DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
      .dockerHost(config.getDockerHost())
      .sslConfig(config.getSSLConfig())
      .build();
    dockerClient = DockerClientImpl.getInstance(config, httpClient);
  }

  @AfterEach
  void tearDown() {
    // Clean up - try to remove the container
    try {
      dockerClient.stopContainerCmd(CONTAINER_NAME).exec();
      dockerClient.removeContainerCmd(CONTAINER_NAME).exec();
    } catch (Exception e) {
      // Ignore exceptions during cleanup
    }
  }

  @Test
  void testRunStopContainer() throws InterruptedException {

    // Run the container
    dockerContainer.run();

    // Verify container is running
    assertTrue(dockerContainer.isRunning(), "Container should be running after run() call");

    // Run it again, should not throw any errors
    assertDoesNotThrow(() -> dockerContainer.run(), "Running an already running container should not throw errors");

    // Stop the container
    dockerContainer.stop();

    // Wait a moment for the container to stop
    Thread.sleep(1000);

    // Verify container is stopped
    assertFalse(dockerContainer.isRunning(), "Container should be stopped after stop() call");

    // Remove the container
    dockerContainer.rm();

    // Verify container is removed
    assertThrows(NotFoundException.class, () -> dockerClient.inspectContainerCmd(CONTAINER_NAME).exec(),
      "Container should be removed after rm() call");
  }
}
